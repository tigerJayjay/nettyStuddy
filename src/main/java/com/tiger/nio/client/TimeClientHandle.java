package com.tiger.nio.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable{
    private Selector selector;
    private volatile boolean stop;
    private SocketChannel socketChannel;
    private String host;
    private int port;

    public TimeClientHandle(String host,int port){
        try {
            this.host = host;
            this.port = port;
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        //连接服务端
        try{
            doConnetct();
        }catch (IOException e){
            e.printStackTrace();
        }

        //开始轮询多路复用器的selectKey，监听事件
        while(!stop){
            try{
                //设置超时时间
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it =  selectedKeys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    //处理key事件，将它从set中去除
                    it.remove();
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        //关闭资源
                        if(key!=null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //当主动关闭轮询，关闭多路复用器,同时会关闭注册在上边的Channel和Pipe等资源
        if(selector != null){
            try{
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void doConnetct()throws  IOException{
        //connet方法不一定会返回true,通常在本地连接的情况立即返回true，但是也可能返回false,之后必须通过调用channel.finisheConnect()方法完成连接操作
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            //连接成功，将SocketChannel注册到多路复用器上，发送请求消息，读应答消息
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            //没有连接成功，说明服务端没有返回TCP握手应答消息，不代表连接失败，将SocketChannel注册到多路复用器上，注册连接事件，当服务端返回TCPsyn-ack消息后
            //Selector就能够轮询到这个SocketChannel处于连接就绪状态
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void handleInput(SelectionKey key) throws IOException{
        //检验key
        if(key.isValid()){
           SocketChannel channel =  (SocketChannel)key.channel();
           //如果为true，说明服务端已经返回ACK应答消息，需要调用SocketChannel的finishConnet()方法对结果进行判断
           if(key.isConnectable()){
                if(channel.finishConnect()){
                    channel.register(selector,SelectionKey.OP_READ);
                    doWrite(channel);
                }else{
                    System.exit(1);
                }
           }
           if(key.isReadable()){
               ByteBuffer buffer = ByteBuffer.allocate(1024);
               int readBytes = channel.read(buffer);
               if(readBytes>0){
                   buffer.flip();
                   byte[] bytes = new byte[buffer.remaining()];
                   buffer.get(bytes);
                   String body = new String(bytes, StandardCharsets.UTF_8);
                   System.out.println("NOW IS : "+body);
                   this.stop = true;
               }else if(readBytes<0){
                   key.cancel();
                   channel.close();
               }
           }
        }
    }

    private void doWrite(SocketChannel channel)throws  IOException{
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(req.length);
        buffer.put(req);
        buffer.flip();
        channel.write(buffer);
        if(!buffer.hasRemaining()){
            System.out.println("Send order 2 server succeed.");
        }
    }
}
