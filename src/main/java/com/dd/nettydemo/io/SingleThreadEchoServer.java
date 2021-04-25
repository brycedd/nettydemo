package com.dd.nettydemo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class SingleThreadEchoServer {

    private final int port;

    public SingleThreadEchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new SingleThreadEchoServer(8080).startServer();
    }


    public void startServer() {
        ServerSocket echoServer;
        int i = 0;
        System.out.println("服务器在端口[" + this.port + "]等待客户请求.....");
        try {
            //创建服务端socket
            echoServer = new ServerSocket(this.port);
            for(;;) {
                //自旋接收来自客户端的消息
                Socket clientSocket = echoServer.accept();
                //处理客户端消息
                handleRequest(clientSocket,i++);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket, int clientNo) {
        //创建打印流
        PrintStream os;
        //创建读取消息Buffered
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                //输入'Quit'退出
                if(inputLine.equals("Quit")) {
                    System.out.println("关闭与客户端[" + clientNo + "] ......" + clientNo);
                    os.close();
                    in.close();
                    clientSocket.close();
                    break;
                }else {
                    System.out.println("来自服务器[" + clientNo + "]的消息：" + inputLine);
                    //控制台输入回复
                    BufferedReader systemIn = new BufferedReader(new InputStreamReader(
                            System.in));
                    System.out.println("请输入回复：");
                    os.println("来自服务器端的响应：" + systemIn.readLine());
                }
            }
        } catch (IOException e) {
            System.out.println("Stream closed");
        }
    }
}
