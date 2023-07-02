package com.taki.cloud.rpc.reference2;

import com.taki.cloud.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName NettyRpcReadTimeoutHandler
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 16:29
 * @Version 1.0
 */
public class NettyRpcReadTimeoutHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LogManager.getLogger(NettyRpcReadTimeoutHandler.class);

    private long timeout;

    public NettyRpcReadTimeoutHandler(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse) msg;

        long responseTime = NettyRpcRequestTimeHolder.get(rpcResponse.getRequestId());

        long now = System.currentTimeMillis();

        if (now - responseTime >= timeout){
            rpcResponse.setSuccess(RpcResponse.SUCCESS);
            logger.error("netty rpc  presponse is marked  as  timeout status:{}",rpcResponse);
        }

        NettyRpcRequestTimeHolder.remove(rpcResponse.getRequestId());

        ctx.fireChannelRead(rpcResponse);
    }
}
