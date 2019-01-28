package com.tiger.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class TimeClient {
    private EventLoopGroup workerGroup;
    private Bootstrap b;
    private Channel channel;
    private  String host = "132.232.103.107";
    private int port = 8082;
    public  void connect() throws Exception{
        workerGroup = new NioEventLoopGroup();
            b = new Bootstrap();
            //bossGroup不用于客户端
            b.group(workerGroup);
            //服务端用的是NioServerSocketChannel
            b.channel(NioSocketChannel.class);
            //没有使用childOption()，因为客户端SocketChannel没有parent
            b.option(ChannelOption.SO_KEEPALIVE,true);
            b.handler(new ChannelInitializer<SocketChannel>() {
               /* @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new TcpHalfPackageTestHandler());
                }*/

//                @Override
//                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    socketChannel.pipeline().addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//                    socketChannel.pipeline().addLast(new ObjectEncoder());
//                    socketChannel.pipeline().addLast(new NettyObjectDecoderTestHandler());
//                }


                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                   /* socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                    socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    socketChannel.pipeline().addLast(new ProtobufEncoder());
                    socketChannel.pipeline().addLast(new ProtoBufHandler());*/
                    socketChannel.pipeline().addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                    socketChannel.pipeline().addLast(new ObjectEncoder());
                    socketChannel.pipeline().addLast(new IdleStateHandler(0,0,30));
                    socketChannel.pipeline().addLast(new NettyObjectDecoderTestHandler());
                }
            });
           doConnect();
    }
    public void doConnect(){
        if(channel!=null&&channel.isActive()){
            return;
        }
        ChannelFuture f= b.connect(host,port);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    channel = channelFuture.channel();
                    System.out.println("连接服务器成功");
                }else{
                    System.out.println("10秒后重连");
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    },10, TimeUnit.SECONDS);
                }
            }
        });
    }
    public static void main(String[] args)  throws Exception{
            new TimeClient().connect();
    }

}
