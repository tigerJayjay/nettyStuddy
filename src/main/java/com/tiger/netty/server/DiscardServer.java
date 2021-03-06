package com.tiger.netty.server;

import com.tiger.netty.protofile.SubscribeReqProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 服务端启动类
 */
public class DiscardServer {
    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws  Exception{
        //NioEventLoopGroup是一个处理I/0操作的多线程事件轮询器，Nettty为不同的协议提供了多种实现
        //bossGroup经常被叫做老板，接受一个即将到来的连接
        //workerGroup叫做工人，一旦老板接收并注册了连接给工人，那么就开始处理接受连接的流量
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //ServerBootstrp是一个帮助类，你也可以通过Channel直接设置服务器信息，但是它是一个非常繁琐的过程
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)//调用channel()方法通过new ReflectiveChannelFactory(channelClass)实例化channel工厂
                    .childHandler(new ChannelInitializer<SocketChannel>() {


                        //ChannelPipeline会构建一个Channel的Handler堆栈，包含入站处理器和出站处理器,当一个事件入站会依次通过入站处理的的评估，当一个事件出站会依次通过出战处理器的评估
                        //ChannelPipeline会根据事件出入情景，自动跳过没有实现ChannelOutboundHandler或者ChannelInboundHandler的处理器来缩短栈长
                        //当一个处理器既实现ChannelInboundHandler又实现ChannelOutboundHandler那么按照设置的顺序值按次序处理事件
                        //可以通过handler来decoder或者encoder你自己的协议格式
                        //通过LineBasedFrameDecoder()处理粘包/分包问题
                      /*
                      @Override
                      protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new TcpHalfPackTestHandler());
                        }*/
                        //通过ObjectDecoder和ObjectEncoder()进行对象序列化和反序列化
                       /* @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            socketChannel.pipeline().addLast(new ObjectEncoder());
                            socketChannel.pipeline().addLast(new NettyObjectDecoderHandler());
                        }*/
                        //通过google protoBuf对java对象编解码
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //ProtoBufVarint32FrameDecoder()用于半包处理 解码
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            //参数需要传入需要解码对象
                            socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                            //编码
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new ProtoBufHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //绑定端口，开始接收即将到来的连接
            ChannelFuture f = b.bind(port).sync();//->doBind()->initAndRegister()->

            //等待服务端socket被关闭
            //在这个例子中，不会发生，但是你可以通过这样做去优雅的关闭你的服务端
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception{
        int port = 8080;
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }
        new DiscardServer(port).run();
    }
}
