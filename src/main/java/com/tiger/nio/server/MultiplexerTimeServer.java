package com.tiger.nio.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements  Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;


    public MultiplexerTimeServer(int port){
        try{
            //2.创建多路复用器
            selector = Selector.open();
            //3.打开ServerSocketChannel,用于监听客户端的连接,它是所有客户端连接的父管道
            serverSocketChannel = ServerSocketChannel.open();
            //4.绑定监听端口，设置连接为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            //5.将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听Accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void stop(){
        this.stop = true;
    }
    @Override
    public void run() {
        //6.多路复用器在线程run方法无限循环体内轮询准备就绪的Key
        while(!stop){
            try{
                selector.select(1000);
               Set<SelectionKey> selectedKeys =  selector.selectedKeys();
               Iterator<SelectionKey> it = selectedKeys.iterator();
               SelectionKey selectionKey = null;
               while(it.hasNext()){
                   selectionKey = it.next();
                   it.remove();
                   try{
                       handleInput(selectionKey);
                   }catch (Exception e){
                      if(selectionKey!=null){
                          selectionKey.cancel();
                          if(selectionKey.channel()!=null){
                              selectionKey.channel().close();
                          }
                      }
                   }
               }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //多路复用器关闭后，所有注册在上边的Channel和Pipe等资源都会被自动去注册，并关闭，所以不需要重复释放资源
        if(selector!=null){
            try{
                selector.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void  handleInput(SelectionKey key) throws  IOException{
        if(key.isValid()){
            if(key.isAcceptable()){
                //接受连接
                ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                //6.多路复用器监听到有新的客户端接入，处理新的接入请求,完成TCP三次握手，建立物理链路
                SocketChannel sc = ssc.accept();
                //7.设置客户端链路为非阻塞模式
                sc.configureBlocking(false);
                //8.将新介入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，用来读取客户端发送的网络消息
                sc.register(selector,SelectionKey.OP_READ);
            }
            if(key.isReadable()){
                //读取数据
                SocketChannel sc = (SocketChannel)key.channel();
               ByteBuffer readBuffer = ByteBuffer.allocate(1024);
               //9.读取通道中的数据到缓冲区，大小最大为缓冲区中remaining()的大小（position到limit)
               int readBytes = sc.read(readBuffer);
               if(readBytes>0){
                    //将limit设置为当前position,position设置为0
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("The time server receive order: "+body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
                    doWrite(sc,currentTime);
               }else if(readBytes<0){
                   //链路关闭
                   key.cancel();
                   sc.close();
               }
            }
        }
    }

    private void doWrite(SocketChannel channel,String response) throws IOException{
        if(response!=null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            //limit置为position，position置为0
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }

}
