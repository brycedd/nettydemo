package com.dd.nettydemo.demo2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author Bryce
 * @date 2021/4/25
 */
public class WebsocketDanmuServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-decoder",new HttpRequestDecoder());
        pipeline.addLast("http-aggregator",new HttpObjectAggregator(65536));
        pipeline.addLast("http-encoder",new HttpResponseEncoder());
        pipeline.addLast("http-chunked",new ChunkedWriteHandler());



        //http 业务处理
        pipeline.addLast("http-request",new HttpRequestHandler("/ws"));
        //实现websocket协议的编码与解码
        pipeline.addLast("WebSocket-protocol",new WebSocketServerProtocolHandler("/ws"));

        //实时弹幕发送业务
        pipeline.addLast("websocket-request",new TextWebSocketFrameHandler());

    }
}
