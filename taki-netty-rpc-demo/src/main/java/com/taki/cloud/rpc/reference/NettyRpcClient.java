package com.taki.cloud.rpc.reference;


import com.taki.cloud.rpc.RpcEncoder;
import com.taki.cloud.rpc.RpcRequest;
import com.taki.cloud.rpc.RpcResponse;
import com.taki.cloud.rpc.sevice.RpcDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class NettyRpcClient {

    private static final Logger logger = LogManager.getLogger(NettyRpcClient.class);

    private ReferenceConfig referenceConfig;
    private ChannelFuture channelFuture;
    private NettyRpcClientHandler nettyRpcClientHandler;

    public NettyRpcClient(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
        this.nettyRpcClientHandler = new NettyRpcClientHandler(
                referenceConfig.getTimeout());
    }

    public void connect() {
        logger.info("connecting to netty rpc server: " +
                referenceConfig.getServiceHost() + ":" + referenceConfig.getServicePort());

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder(RpcRequest.class))
                                .addLast(new RpcDecoder(RpcResponse.class))
                                .addLast(new NettyRpcReadTimeoutHandler(referenceConfig.getTimeout()))
                                .addLast(nettyRpcClientHandler);
                    }
                });

        try {
            if(referenceConfig.getServiceHost() != null && !referenceConfig.getServiceHost().equals("")) {
                channelFuture = bootstrap.connect(referenceConfig.getServiceHost(), referenceConfig.getServicePort()).sync();
                logger.info("successfully connected.");
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RpcResponse remoteCall(RpcRequest rpcRequest) throws Throwable {
        NettyRpcRequestTimeHolder.put(rpcRequest.getRequestId(), new Date().getTime());

        channelFuture.channel().writeAndFlush(rpcRequest).sync();

        RpcResponse rpcResponse = nettyRpcClientHandler.getRpcResponse(
                rpcRequest.getRequestId());

        if(rpcResponse.getSuccess()) {
            return rpcResponse;
        }

        throw rpcResponse.getException();
    }

}
