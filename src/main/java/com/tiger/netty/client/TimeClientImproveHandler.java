package com.tiger.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * 因为基于流传输的协议，会有可能将你发送的多个独立的信息当成一堆字节分片传输，因此不保证，你读到的就是远程对端发送的信息
 * 比如你发送的是ABC  DEF GHI 但是经过分片之后你可能接受的是ABCD EF G  HI
 * 会报数组下标越界异常
 * 简单的解决方案是创建一个内部累积缓冲区，并等待所有4个字节都被接收到内部缓冲区
 */
public class TimeClientImproveHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf buf;
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        buf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        buf.release();
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf)msg;
        buf.writeBytes(m);
        m.release();
        //这里等缓冲区装满4个字节之后才会进入业务逻辑，防止数组下标越界异常
        if(buf.readableBytes()>=4){
            long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
