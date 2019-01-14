package com.tiger.nio.client;

public class TimeClient {
    public static void main(String[] args){
        int port = 8080;
        if(args!=null && args.length>0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        new Thread(new TimeClientHandle("localhost",port),"TIMECLIENT=001").start();
    }

}
