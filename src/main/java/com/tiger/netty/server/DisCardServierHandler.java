package com.tiger.netty.server;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 服务端处理类
 */
public class DisCardServierHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当收到客户端信息时这个方法会被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //1.丢弃服务器：ByteBuf是一个引用计数对象,释放所有传到handler里的引用计数对象是handler的职责
        //((ByteBuf)msg).release();
        //2.通常handler方法会像下面这样实现（此处为接收客户端数据打印到控制台）
        /*ByteBuf in = (ByteBuf)msg;
        try{
            while(in.isReadable()){
               //下面代码可以简化为System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
                System.out.println((char)in.readByte());
                System.out.flush();
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }*/
        //3.响应服务器：接收数据返回给客户端,此处并没有像之前那样release掉msg
        // 这是因为netty自动帮你释放
        //使用write()方法不会将数据返回，只是将数据存到内部缓存，调用flush()才会将数据返回
        //也可以调用ctx.writeAndFlush()
        ctx.write(msg);
        ctx.flush();
    }

    /**
     * 当netty由于出现I/O异常或者实现了Handler的类在处理事件时抛出异常时这个方法会被调用
     * 出现异常时的具体处理措施由自己决定
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
