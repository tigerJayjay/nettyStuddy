package com.tiger.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 实现一个时间服务器（发送一个32位整数的数据代表当前时间，不需要接收任何数据，一旦数据被发送，就会断开连接
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当连接被建立并将要产生流量时被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        //因为要返回32位整数，所以要申请一个4字节的缓冲区存储信息
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int)(System.currentTimeMillis() / 1000L + 2208988800L));
        //write()或writeAndFlush()方法返回一个ChannelFuture,它代表着一个还没有发生的I/0操作
        //在netty中，所有的请求操作都有可能还没有执行，因为所有的操作都是异步的
        //如果在write()或者writeAndFlush()方法后直接调用close()方法，也许在信息发送之前就关闭了连接
        //因此需要在write()方法返回的ChannelFutrue完成之后调用close()方法，通知ChannelFuture的监听器，操作已经完成了，可以关闭
        //下面代码可以简化为f.addListenre(ChannelFutureListener.CLOSE);
        final ChannelFuture  f =  ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert f == channelFuture;
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
       ctx.close();
    }
}
