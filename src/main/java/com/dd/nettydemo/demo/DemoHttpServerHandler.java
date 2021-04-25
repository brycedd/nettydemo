package com.dd.nettydemo.demo;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.List;

/**
 * @author Bryce
 * @date 2021/4/24
 */
public class DemoHttpServerHandler extends SimpleChannelInboundHandler<Object> {

//    DefaultHttpRequest request;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){

        //response
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //写入请求头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
        //写入请求体
        response.content().writeBytes("ddtt00".getBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    //将content和request放在一起：FullHttpRequest
    protected void channelReadFullRequest(ChannelHandlerContext ctx, FullHttpRequest msg){

        List<InterfaceHttpData> list;


        //不使用FullHttpRequest
        //获取参数
//        if(msg instanceof HttpContent) {
//            HttpPostRequestDecoder decoder =
//                    new HttpPostRequestDecoder(request);
//            decoder.offer((HttpContent) msg);
//            list = decoder.getBodyHttpDatas();
//        }
//        if(msg instanceof DefaultHttpRequest){
//            request = (DefaultHttpRequest) msg;
//        }

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(msg.retain());
        //decoder.offer(msg.);//取出content

        //放入list
        list = decoder.getBodyHttpDatas();


        //response
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //写入请求头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
        //写入请求体
        response.content().writeBytes("ddtt00".getBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    //大文件上传
    protected void channelReadBigFile(ChannelHandlerContext ctx, Object msg){

        if(msg instanceof LastHttpContent) {//当是最后一个content时才返回并关闭
            //response
            FullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            //写入请求头
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
            //写入请求体
            response.content().writeBytes("ddtt00".getBytes());

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
