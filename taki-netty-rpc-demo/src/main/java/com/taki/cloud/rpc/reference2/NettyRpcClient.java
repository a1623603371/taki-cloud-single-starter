package com.taki.cloud.rpc.reference2;

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

/**
 * @ClassName NettyRpcClient
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 15:56
 * @Version 1.0
 */
public class NettyRpcClient {

    private static final Logger logger = LogManager.getLogger(NettyRpcClient.class);


    private ReferenceConfig referenceConfig;

    private ChannelFuture channelFuture;

    private NettyRpcClientHandler nettyRpcClientHandler;

    public NettyRpcClient(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
        this.nettyRpcClientHandler = new NettyRpcClientHandler(referenceConfig.getTimeout());
    }


    public void connect(){
        logger.info("connect to  netty  rpc  server:{}:{}",referenceConfig.getServiceHost(),referenceConfig.getServicePort());

        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new RpcDecoder(RpcRequest.class))
                        .addLast(new RpcEncoder(RpcResponse.class))
                        .addLast(new NettyRpcReadTimeoutHandler(referenceConfig.getTimeout()))
                        .addLast(nettyRpcClientHandler);
            }
        });

        try {
            if (referenceConfig.getServiceHost() != null && !referenceConfig.getServiceHost().equals("")){
                channelFuture = bootstrap.connect(referenceConfig.getServiceHost(), referenceConfig.getServicePort()).sync();
                logger.info("successfully  connected.");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public RpcResponse remoteCall(RpcRequest rpcRequest) throws Throwable {
        NettyRpcRequestTimeHolder.put(rpcRequest.getRequestId(),System.currentTimeMillis());

        channelFuture.channel().writeAndFlush(rpcRequest).sync();

        RpcResponse response = nettyRpcClientHandler.getRpcResponse(rpcRequest.getRequestId());

        if (response.getSuccess()){
            return response;
        }

        throw response.getException();
    }
}
