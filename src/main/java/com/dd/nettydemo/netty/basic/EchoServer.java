package com.dd.nettydemo.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class EchoServer {

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }

        new EchoServer().bind(port);
    }

    private void bind(int port) throws InterruptedException {
        //第一步：
        //配置服务器端的NIO线程组
        //主线程组，用于接收客户端的连接，但是不做任何具体业务处理；
        NioEventLoopGroup parentGroup = new NioEventLoopGroup(1);
        //工作线程组，主线程会把任务丢给工作线程组进行具体的操作
        NioEventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            //类似ServerBootstrap用于配置Server相关参数，并启动Server
            ServerBootstrap bootstrap = new ServerBootstrap();

            //链式调用
            //配置parentGroup和childGroup
            bootstrap.group(parentGroup,childGroup)
                    //配置Server通道
                    .channel(NioServerSocketChannel.class)
                    //配置通道的ChannelPipeline
                    .childHandler(new ChildChannelHandler());

            // 绑定端口，并启动server，同时设置启动方式为同步
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println(EchoServer.class.getName() +
                    "启动成功，在地址["+future.channel().localAddress()+"]上等待客户请求......");

            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅的退出，释放线程池资源
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) {
            socketChannel.pipeline().addLast(new EchoServerHandler());
        }
    }
}
