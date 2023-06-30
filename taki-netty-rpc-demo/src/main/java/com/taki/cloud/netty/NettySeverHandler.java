package com.taki.cloud.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName NettySeverHandler
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 12:42
 * @Version 1.0
 */
public class NettySeverHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel  active ......");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channel read:" + (String) msg);

        String response = "hello word.........";

        ByteBuf responseByteBuf = Unpooled.buffer();
        responseByteBuf.writeBytes(response.getBytes());

        ctx.channel().writeAndFlush(responseByteBuf);

        System.out.println("channel write:" + response);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel  read  complete .....");

        ctx.flush();
    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
    }
}
