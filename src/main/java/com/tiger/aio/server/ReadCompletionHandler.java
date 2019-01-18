package com.tiger.aio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;
    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        if(this.channel==null){
            this.channel = channel;
        }
    }
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //5.客户端数据读取完成，数据现在在attachment中
        attachment.flip();
        //6.读取attachment中的数据
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String req = new String(body, StandardCharsets.UTF_8);
        System.out.println("The time server receive the order : "+req);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
        //7.向客户端发送数据
        doWrite(currentTime);
    }

    private void doWrite(String currentTime){
        if(currentTime!=null && currentTime.trim().length()>0){
            byte[] time = currentTime.getBytes();
            ByteBuffer res = ByteBuffer.allocate(time.length);
            //8.数据放到缓冲区
            res.put(time);
            res.flip();
            //9.写入通道
            channel.write(res, res, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    //10.防止发半包，如果缓冲区没有发送完成，继续调用发送
                    if(attachment.hasRemaining()){
                        //如果没有发送
                        channel.write(attachment,attachment,this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
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
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
