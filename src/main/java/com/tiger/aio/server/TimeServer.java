package com.tiger.aio.server;

import com.tiger.nio.server.MultiplexerTimeServer;

public class TimeServer {
    public static void main(String[] args){
        int port = 8080;
        if(args!=null && args.length >0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        //1.创建Reactor线程，并启动
        new Thread(timeServer,"IO-TIMERSERVER-001").start();
    }
}
