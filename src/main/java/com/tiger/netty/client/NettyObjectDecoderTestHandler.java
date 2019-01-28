package com.tiger.netty.client;

import com.easybug.netty.protocal.Message;
import com.easybug.netty.protocal.Type;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NettyObjectDecoderTestHandler extends ChannelInboundHandlerAdapter {
    private static int count;
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Scanner scanner = new Scanner(System.in);
                    String a = scanner.next();
                    Message m = new Message();
                    m.setType(Type.Group);
                    Map message = new HashMap();
                    message.put("message", a);
                    m.setAttachment(message);
                    ctx.writeAndFlush(m);
                }
            }
        }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message srs = (Message) msg;
        System.out.println(srs.getSrcId()+":"+srs.getAttachment());
        if(srs.getType().equals(Type.HeatResp)){
            count = 0;
        }else if(srs.getType().equals(Type.HeatReq)){
            srs.setType(Type.HeatResp);
            ctx.channel().writeAndFlush(srs);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent)evt;
            if(e.state()== IdleState.ALL_IDLE){
                allIdleHandler(ctx);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("开始重连...");
        new TimeClient().connect();
    }

    private void allIdleHandler(ChannelHandlerContext ctx) throws Exception{
        Message m = new Message();
        m.setType(Type.HeatReq);
        ctx.writeAndFlush(m);
        //发送超过五次心跳包，服务端未响应，链路损坏发起重连
        if(++count>5){
            ctx.close();
            new TimeClient().connect();
        }
    }
}
