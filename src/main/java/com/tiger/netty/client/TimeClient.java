package com.tiger.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TimeDecoder(),new TimeClientHandler());
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
