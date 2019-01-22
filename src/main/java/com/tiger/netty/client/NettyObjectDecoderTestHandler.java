package com.tiger.netty.client;

import com.tiger.netty.object.SubscribeReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyObjectDecoderTestHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0;i<100;i++){
            SubscribeReq sr = new SubscribeReq();
            setObject(sr);
            ctx.writeAndFlush(sr);
        }
        ctx.close();
    }

    private void setObject(SubscribeReq s){
        s.setSubReqId(111);
        s.setUserName("liujie");
        s.setProductName("test");
    }
}
