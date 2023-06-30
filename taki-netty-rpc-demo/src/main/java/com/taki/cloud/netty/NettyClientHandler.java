package com.taki.cloud.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName NettyClientHandler
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 12:58
 * @Version 1.0
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf pingByteBuf;

    public NettyClientHandler() {
        String ping = "ping .....";

        pingByteBuf = Unpooled.buffer(ping.getBytes().length);

        pingByteBuf.writeBytes(ping.getBytes());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(pingByteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("received  response:" +msg);
        ctx.channel().close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
       ctx.close();
    }
}
