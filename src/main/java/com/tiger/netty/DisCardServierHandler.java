package com.tiger.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务端处理类
 */
public class DisCardServierHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当收到客户端信息时这个方法会被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf是一个引用计数对象,释放所有传到handler里的引用计数对象是handler的职责
        ((ByteBuf)msg).release();
        //通常handler方法会像下面这样实现
        /*   try{

        }finally {
            ReferenceCountUtil.release(msg);
        }*/
    }

    /**
     * 当netty由于出现I/O异常或者实现了Handler的类在处理事件时抛出异常时这个方法会被调用
     * 出现异常时的具体处理措施由自己决定
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
