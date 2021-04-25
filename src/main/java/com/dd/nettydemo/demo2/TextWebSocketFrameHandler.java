package com.dd.nettydemo.demo2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Bryce
 * @date 2021/4/25
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    //客户连接池
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if(channel != incoming) {
                channel.writeAndFlush(new TextWebSocketFrame(msg.text()));
            }else {
                channel.writeAndFlush(new TextWebSocketFrame("dd:" + msg.text()));
            }
        }
    }

    //当一个连接进来后会触发此方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        channels.writeAndFlush(
                new TextWebSocketFrame("[server] - " + incoming.remoteAddress() + "加入"));

        channels.add(incoming);
        System.out.println("Client:" + incoming.remoteAddress() + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        channels.writeAndFlush(new TextWebSocketFrame("[server]" + incoming.remoteAddress() + "离开"));
        System.err.println("Client:" + incoming.remoteAddress() + "离开");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.err.println("Client:" + channel.remoteAddress() + "在线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        System.err.println("Client:" + channel.remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
}
