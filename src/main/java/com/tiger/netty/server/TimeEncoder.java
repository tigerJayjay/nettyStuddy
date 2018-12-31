package com.tiger.netty.server;

import com.tiger.netty.protocal.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class TimeEncoder extends ChannelOutboundHandlerAdapter {
    //1.传递原始的ChannelPromise,以便于netty在将编码的数据写入到流里面时可以将它标记为成功或失败
    //2.没有调用ctx.flush()方法，是因为有一个单独的flush(ctx)方法可以重写
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        UnixTime m = (UnixTime) msg;
        ByteBuf encoded = ctx.alloc().buffer(4);
        encoded.writeInt((int)m.value());
        ctx.write(encoded,promise);
    }

}
