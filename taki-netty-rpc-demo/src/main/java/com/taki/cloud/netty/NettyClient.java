package com.taki.cloud.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @ClassName NettyClient
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 12:49
 * @Version 1.0
 */
public class NettyClient {

    private static final String HOST = "127.0.0.1";

    private  static  final  Integer PORT = 8998;


    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new StringDecoder())
                                .addLast(new StringEncoder()).addLast(new NettyClientHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(HOST,PORT).sync();

            String request = "hello netty.....";
            future.channel().writeAndFlush(request);
            future.channel().writeAndFlush(Unpooled.copiedBuffer("hello word .........", CharsetUtil.UTF_8));

            future.channel().closeFuture().sync();
        }finally {
                group.shutdownGracefully();
        }


    }
}
