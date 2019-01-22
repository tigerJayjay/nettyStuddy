package com.tiger.netty.server;

import com.tiger.netty.object.SubscribeReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 测试netty自带的ObjectEncoder和ObjectDecoder对对象序列化和反序列化,同时测试是否支持粘包/分包
 */
public class NettyObjectDecoderHandler extends ChannelInboundHandlerAdapter {
    private int count;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq sr = (SubscribeReq)msg;
        System.out.println(sr.toString()+ ++count);
    }
}
