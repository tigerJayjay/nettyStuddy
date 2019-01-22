package com.tiger.netty.client;

import com.tiger.netty.protofile.SubscribeReqProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {
    public static void main(String[] args)  throws Exception{
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            Bootstrap b = new Bootstrap();
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
                    socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                    socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    socketChannel.pipeline().addLast(new ProtobufEncoder());
                    socketChannel.pipeline().addLast(new ProtoBufHandler());
                }
            });
            //服务端使用bind()
            ChannelFuture f = b.connect(host,port).sync();


            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}
