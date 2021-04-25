package com.dd.nettydemo.nio.demo;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class NIOEchoServer {

    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //
            }
        }

        EchoHandler timeServer = new EchoHandler(port);
        new Thread(timeServer,"NIO-MultiplexerTimeServer-001").start();
    }

}
