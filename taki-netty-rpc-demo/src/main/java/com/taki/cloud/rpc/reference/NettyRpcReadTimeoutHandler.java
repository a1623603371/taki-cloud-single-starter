package com.taki.cloud.rpc.reference;


import com.taki.cloud.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class NettyRpcReadTimeoutHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(NettyRpcReadTimeoutHandler.class);

    private long timeout;

    public NettyRpcReadTimeoutHandler(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse)msg;

        long requestTime = NettyRpcRequestTimeHolder.get(rpcResponse.getRequestId());
        long now = new Date().getTime();

        if(now - requestTime >= timeout) {
            rpcResponse.setTimeout(true);
            logger.error("netty rpc response is marked as timeout status: " + rpcResponse);
        }

        NettyRpcRequestTimeHolder.remove(rpcResponse.getRequestId());

        ctx.fireChannelRead(rpcResponse);
    }
}
