package com.dd.nettydemo.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class DemoHttpServer {

    public static void main(String[] args) {
        new DemoHttpServer().openServer(8080);
    }

    public void openServer(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);


        NioEventLoopGroup boot = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup(2);
        bootstrap.group(boot, work);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //request解码handler
                ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                //response编码handler
                ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                //封装成fullHttp 对应handler中启用FullRequest时启用
                //ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                //业务处理handler
                ch.pipeline().addLast("http-servlet", new DemoHttpServerHandler());
            }
        });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务已启动:" + port);
            //等到管道关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅的退出，释放线程池资源
            boot.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
