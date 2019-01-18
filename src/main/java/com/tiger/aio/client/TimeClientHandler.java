package com.tiger.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class TimeClientHandler implements Runnable, CompletionHandler<Void, TimeClientHandler> {

    private AsynchronousSocketChannel channel;
    private int port;
    private String host;
    private CountDownLatch countDownLatch;
    public TimeClientHandler(String host,int port){
        this.host = host;
        this.port = port;
        try {
            channel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
            //1.与服务端建立连接
            channel.connect(new InetSocketAddress(host,port),this,this);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void completed(Void result, TimeClientHandler attachment) {
        //2.连接成功，发送信息
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer bf = ByteBuffer.allocate(req.length);
        bf.put(req);
        bf.flip();
        channel.write(bf, bf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(attachment.hasRemaining()){
                    channel.write(attachment,attachment,this);
                }else{
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    channel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] bytes = new byte[attachment.remaining()];
                            attachment.get(bytes);
                            String body = new String(bytes, StandardCharsets.UTF_8);
                            System.out.println("Server Time Is : "+body);
                            countDownLatch.countDown();
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                channel.close();
                                countDownLatch.countDown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                    countDownLatch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, TimeClientHandler attachment) {
        try {
            channel.close();
            countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
