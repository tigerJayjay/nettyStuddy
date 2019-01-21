package com.tiger.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TcpHalfPackageTestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] req = ("hihihi"+System.getProperty("line.separator")).getBytes();
        for(int i = 0;i<100;i++){
            ByteBuf b  = Unpooled.buffer(req.length);
            b.writeBytes(req);
            ctx.writeAndFlush(b);
        }
        ctx.close();
    }
}
