package com.tiger.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncTimeServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        //3.连接成功，继续调用accept()方法，接受其他新的客户端连接，形成一个循环
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer buffer  = ByteBuffer.allocate(1024);
        //4.开始读取客户端传输的数据到buffer中，并传入读取成功的回调接口
        result.read(buffer,buffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
