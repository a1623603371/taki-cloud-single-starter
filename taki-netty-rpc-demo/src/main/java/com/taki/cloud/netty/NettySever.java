package com.taki.cloud.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @ClassName NettySever
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 12:10
 * @Version 1.0
 */
public class NettySever {

    private int port;

    public NettySever(int port) {
        this.port = port;
    }


    public void start() throws InterruptedException {

        EventLoopGroup  bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();


        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {

                    socketChannel.pipeline().addLast(new StringDecoder()).addLast(new StringEncoder()).addLast(new NettySeverHandler());
                }
            }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }


    public static void main(String[] args) throws InterruptedException {

        System.out.println("starting  netty  sever ......");

        int port = 8998;

        if (args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        new NettySever(port).start();
    }
}
