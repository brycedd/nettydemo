package com.dd.nettydemo.nio.demo;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class NIOEchoClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //
            }
        }
        new Thread(new EchoClientHandler("127.0.0.1",port),"NIOEchoClient-001").start();

    }

}
