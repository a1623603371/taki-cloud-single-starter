package com.taki.cloud.rpc.reference;


import com.taki.cloud.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(NettyRpcClientHandler.class);
    private static final long GET_RPC_RESPONSE_SLEEP_INTERVAL = 5;

    private ConcurrentHashMap<String, RpcResponse> rpcResponses =
            new ConcurrentHashMap<String, RpcResponse>();
    private long timeout;

    public NettyRpcClientHandler(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse) msg;
        if(rpcResponse.getTimeout()) {
            logger.error("netty rpc client receives the response timeout: " + rpcResponse);
        } else {
            rpcResponses.put(rpcResponse.getRequestId(), rpcResponse);
        }
    }

    public RpcResponse getRpcResponse(String requestId) throws NettyRpcReadTimeoutException {
        long waitStartTime = new Date().getTime();

        while(rpcResponses.get(requestId) == null) {
            try {
                long now = new Date().getTime();
                if(now - waitStartTime >= timeout) {
                    break;
                }
                Thread.sleep(GET_RPC_RESPONSE_SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                logger.error("wait for response interrupted.", e);
            }
        }

        RpcResponse rpcResponse = rpcResponses.get(requestId);

        if(rpcResponse == null) {
            logger.error("get rpc response timeout.");
            throw new NettyRpcReadTimeoutException("get rpc response timeout.");
        } else {
            rpcResponses.remove(requestId);
        }

        return rpcResponse;
    }

}
