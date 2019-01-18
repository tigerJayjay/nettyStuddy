package com.tiger.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements  Runnable{
    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public AsyncTimeServerHandler(int port){
        this.port = port;
        try{
            //1.打开通道，绑定端口
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        latch = new CountDownLatch(1);
        doAccept();
        try{
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void doAccept(){
        //2.接收客户端连接，传入连接成功的处理类
        asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
    }
}
