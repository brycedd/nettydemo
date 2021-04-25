package com.dd.nettydemo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class EchoClient {

    public static void main(String[] args) {
        Socket echoClientSocket;
        PrintStream out;
        BufferedReader in;

        try {
            //创建客户端socket
            echoClientSocket = new Socket("127.0.0.1",8080);
            out = new PrintStream(echoClientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(
                    echoClientSocket.getInputStream()));
            System.out.println("连接到服务器......");
            System.out.println("请输入消息["+"Quit"+"]退出：");
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(
                    System.in));

            String userInput;

            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                System.out.println(in.readLine());

                if(userInput.equals("Quit")) {
                    System.out.println("关闭客户端......");
                    out.close();
                    in.close();
                    systemIn.close();
                    echoClientSocket.close();
                    System.exit(1);
                }
                System.out.println("请输入消息[输入"+"Quit"+"]退出：");
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: PallaviÕs MacBook Pro.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: PallaviÕs MacBook Pro.");
            System.exit(1);
        }


    }
}
