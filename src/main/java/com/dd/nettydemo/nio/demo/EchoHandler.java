package com.dd.nettydemo.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class EchoHandler implements Runnable{

    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;
    private int num = 0;


    public EchoHandler(int port) {

        try {
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port),1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器在端口["+port+"]等待客户请求......");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keys = selectionKeys.iterator();
                SelectionKey key;
                while (keys.hasNext()) {
                    key = keys.next();
                    keys.remove();
                    try {
                        handleInput(key);
                    } catch (IOException e) {
                        if(key != null) {
                            key.cancel();
                            if(key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        // 多路服用器关闭后，所有注册在上面的channel和pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if(selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{

        if(key.isValid()) {
            // 处理新接入的请求消息
            if(key.isAcceptable()) {
                //从selector获取到channel
                ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                SocketChannel socketChannel = ssc.accept();
                socketChannel.configureBlocking(false);
                SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_READ);
                sk.attach(num ++);
            }

            if(key.isReadable()) {
                //读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readByes = sc.read(readBuffer);
                if(readByes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("来自客户端["+key.attachment()+"]的输入：["+body.trim()+"]!");

                    if(body.trim().equals("Quit")) {
                        System.out.println("关闭与客户端["+key.attachment()+"]的连接......");
                        key.cancel();
                        sc.close();
                    }else {
                        doWrite(sc,"来自服务器端的响应：" + body);
                    }
                }else if(readByes < 0) {
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                }else {

                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String response) throws IOException {
        if(response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            sc.write(writeBuffer);
        }
    }


}
