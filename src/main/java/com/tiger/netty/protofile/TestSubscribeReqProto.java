package com.tiger.netty.protofile;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.*;

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
    public int[] deckRevealedIncreasing(int[] deck) {
        int n= deck.length;
        Arrays.sort(deck);
        Queue<Integer> q= new LinkedList<>();
        for (int i=0; i<n; i++) q.add(i);
        int[] res= new int[n];
        for (int i=0; i<n; i++){
            res[q.poll()]=deck[i];
            q.add(q.poll());
        }
        return res;
    }
    public  static void main(String[] args)throws InvalidProtocolBufferException{
      /*  SubscribeReqProto.SubscribeReq req = TestSubscribeReqProto.createSubscribeReq();
        System.out.println(req);

        System.out.println(decode(encode(req)).toString());*/
        TestSubscribeReqProto t = new TestSubscribeReqProto();
        t.deckRevealedIncreasing(new int[]{2,3,5,7,11,13,17});
    }
}
