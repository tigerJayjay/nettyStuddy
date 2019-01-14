package com.tiger.nio.server;

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
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        //1.创建Reactor线程，并启动
        new Thread(timeServer,"NIO-TIMERSERVER-001").start();
    }
}
