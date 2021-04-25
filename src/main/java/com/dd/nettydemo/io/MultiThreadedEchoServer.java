package com.dd.nettydemo.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class MultiThreadedEchoServer {

    private int port;

    public MultiThreadedEchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        new MultiThreadedEchoServer(8080).startServer();
    }

    private void startServer() {
        ServerSocket echoServer;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int i = 0;
        System.out.println("服务器在端口["+this.port+"]等待客户请求......");
        try {
            echoServer = new ServerSocket(port);
            for(;;) {
                Socket clientRequest = echoServer.accept();
                executor.execute(new ThreadedServerHandler(clientRequest,i++));
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
