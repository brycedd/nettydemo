package com.dd.nettydemo.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Bryce
 * @date 2021/4/25
 */
public class WebsocketDanmuServer {

    private int port;

    public WebsocketDanmuServer(int port) {
        this.port = port;
    }


    public void run() throws Exception {
        NioEventLoopGroup bootGroup = new NioEventLoopGroup(2);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(3);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bootGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebsocketDanmuServerInitializer())
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);


            ChannelFuture future = b.bind(port).sync();
            System.out.println("服务端启动：" + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bootGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("服务已关闭：" + port);
        }
    }

    public static void main(String[] args) {
        int port;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }else {
            port = 8080;
        }
    }
}
