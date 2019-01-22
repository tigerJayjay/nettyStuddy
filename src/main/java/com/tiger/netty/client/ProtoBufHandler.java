package com.tiger.netty.client;

import com.tiger.netty.protofile.SubscribeReqProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProtoBufHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       for(int i =0;i<100;i++){
           ctx.writeAndFlush(createSubscribeReq());
       }
       ctx.close();
    }
    private static   SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("liujie");
        builder.setProductName("netty");
        builder.setAddress("BeiJing");
        return builder.build();
    }
}
