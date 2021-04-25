package com.dd.nettydemo.nio.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class EchoClientHandler implements Runnable {
    private final String host;
    private final int port;

    private Selector selector;
    private SocketChannel socketChannel;

    private ExecutorService executorService;

    private volatile boolean stop;

    public EchoClientHandler(String host, int port) {
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;
        this.executorService = Executors.newSingleThreadExecutor();

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {

        try {
            //注册channel到selector上并连接服务器
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(host,port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keys = keySet.iterator();
                SelectionKey key;
                while (keys.hasNext()) {
                    key = keys.next();
                    keys.remove();
                    handleInput(key);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()) {
            //判断是否连接成功
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if(sc.finishConnect()) {
                    System.out.println("连接到服务器......");

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    System.out.println("请输入消息[输入\"Quit\"退出]：");


                    executorService.submit(() -> {
                        for(;;) {
                            try {
                                //写入前清空
                                buffer.clear();
                                InputStreamReader input = new InputStreamReader(System.in);
                                BufferedReader bufferedReader = new BufferedReader(input);

                                String msg = bufferedReader.readLine();

                                if(msg != null && msg.trim().equals("Quit")) {
                                    System.out.println("关闭客户端......");
                                    key.cancel();
                                    sc.close();
                                    this.stop = true;
                                    break;
                                }

                                if(msg != null) {
                                    buffer.put(msg.getBytes());
                                    //翻转并读入channel
                                    buffer.flip();
                                    sc.write(buffer);

                                    System.out.println("请输入消息[输入\"Quit\"退出]：");
                                }else {
                                    System.out.println("请输入消息[输入\"Quit\"退出]：");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    sc.register(selector,SelectionKey.OP_READ);
                }else {
                    //连接失败，进程退出
                    System.exit(1);
                }
            }

            if(key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0) {
                    //翻转为读模式
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);

                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println(body);

                    if(body.trim().equals("Quit")) {
                        this.stop = true;
                    }
                }else if(readBytes < 0) {
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                }else
                    ; //读到0字节，忽略
            }

            if(key.isWritable()) {
                System.out.println("The key is writable");
            }
        }
    }

    private void doWrite(SocketChannel sc,SelectionKey key) throws IOException {
        System.out.println("请输入消息[输入 Quit 退出]：");
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        String userInput;

        while ((userInput = systemIn.readLine()) != null) {

            if(userInput.trim().equals("Quit")) {
                System.out.println("Closing client....");
                key.cancel();
                sc.close();
                this.stop = true;
                break;
            }

            byteBuffer.clear();
            byteBuffer.put(userInput.getBytes(StandardCharsets.UTF_8));
            byteBuffer.flip();
            sc.write(byteBuffer);
            System.out.println("请输入消息[输入\"Quit\"退出]：");
        }
    }


}
