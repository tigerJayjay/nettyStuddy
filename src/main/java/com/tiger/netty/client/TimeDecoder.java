package com.tiger.netty.client;

import com.tiger.netty.protocal.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 第二种解决数组下标越界方法，将Handler拆分为二
 * 1.TimeDecoder 用来解决分片问题
 * 2.TimeClientHandler
 */
public class TimeDecoder extends ByteToMessageDecoder {
    /**
     * 将流解析为自定义的协议对象
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(internalBuffer().readableBytes()<4){
            return;
        }
        list.add(new UnixTime(internalBuffer().readUnsignedInt()));
    }
   /* @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //ByteToMessageDecoder包含一个内部的累加缓冲区,当接收到数据时，调用decode()方法
        //如果缓冲区的数据不够decode()不会添加到list任何东西，当再接收到数据时会再次调用decode()
        //如果decode()方法添加一个对象到list中，说明它已经成功解析了一个信息，ByteToMessageDecoder将会丢弃缓冲区读的部分，然后继续调用decode()方法，直到没有数据为止
        if(internalBuffer().readableBytes()<4){
            return;
        }
        list.add(internalBuffer().readBytes(4));
    }*/
}
