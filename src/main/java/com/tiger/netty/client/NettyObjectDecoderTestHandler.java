package com.tiger.netty.client;

import com.tiger.netty.object.SubscribeReq;
import com.tiger.netty.object.SubscribeRes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyObjectDecoderTestHandler extends ChannelInboundHandlerAdapter {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0;i<100;i++){
            SubscribeReq sr = new SubscribeReq();
            setObject(sr);
            ctx.writeAndFlush(sr);
        }
    }

    private void setObject(SubscribeReq s){
        s.setSubReqId(111);
        s.setUserName("liujie");
        s.setProductName("test");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeRes srs = (SubscribeRes)msg;
        System.out.println(srs.toString()+ ++count);
    }
}
