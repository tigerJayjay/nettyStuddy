package com.tiger.netty.protofile;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestSubscribeReqProto {
    private static byte[] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();
    }
    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }
    private static   SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("liujie");
        builder.setProductName("netty");
        builder.setAddress("BeiJing");
        return builder.build();
    }

    public  static void main(String[] args)throws InvalidProtocolBufferException{
        SubscribeReqProto.SubscribeReq req = TestSubscribeReqProto.createSubscribeReq();
        System.out.println(req);

        System.out.println(decode(encode(req)).toString());
    }
}
