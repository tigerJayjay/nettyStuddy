package com.tiger.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;


public class TcpHalfPackTestHandler extends ChannelInboundHandlerAdapter {
    private int count;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
   /*    ByteBuf b = (ByteBuf)msg;
        byte[] bytes = new byte[b.readableBytes()];
        b.readBytes(bytes);
        System.out.println(new String(bytes, StandardCharsets.UTF_8) + ++count);*/
      String mess = (String)msg;
      System.out.println(mess+ ++count);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
