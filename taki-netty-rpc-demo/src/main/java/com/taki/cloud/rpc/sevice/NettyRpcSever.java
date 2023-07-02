package com.taki.cloud.rpc.sevice;

import com.taki.cloud.rpc.RpcEncoder;
import com.taki.cloud.rpc.RpcRequest;
import com.taki.cloud.rpc.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName NettyRpcSever
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 13:27
 * @Version 1.0
 */
public class NettyRpcSever {
    private static final Logger logger = LogManager.getLogger(NettyRpcSever.class);
    private static final int DEFAULT_PORT = 9001;


    private List<ServiceConfig> serviceConfigs = new CopyOnWriteArrayList<>();
    private int port ;

    public NettyRpcSever( int port) {

        this.port = port;
    }

    public void start(){
        logger.info("netty rpc sever  starting ......");
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast(new RpcDecoder(RpcRequest.class))
                            .addLast(new RpcEncoder(RpcResponse.class))
                            .addLast(new NettyRpcServerHandler(serviceConfigs));
                }
            }).option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("netty rpc server started successfully, listened[" + port + "]");
            // 进入一个阻塞的状态，同步一直等待到你的server端要关闭掉
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("netty rpc server failed to start, listened[" + port + "]");
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void addServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfigs.add(serviceConfig);
    }

    public static void main(String[] args) {
        ServiceConfig serviceConfig = new ServiceConfig("TestService",
                TestService.class, TestServiceImpl.class);

        NettyRpcSever nettyRpcServer = new NettyRpcSever(DEFAULT_PORT);
        nettyRpcServer.addServiceConfig(serviceConfig);
        nettyRpcServer.start();
    }
}
