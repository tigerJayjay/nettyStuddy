package com.tiger.aio.client;

public class TimeClient {
    public static void main(String[] args){
        int port = 8080;
        if(args!=null && args.length>0){
            port = Integer.valueOf(args[0]);
        }
        new Thread(new TimeClientHandler("127.0.0.1",port),"AIO-client").start();
    }
}
