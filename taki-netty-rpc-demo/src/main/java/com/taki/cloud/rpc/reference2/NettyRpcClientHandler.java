package com.taki.cloud.rpc.reference2;

import com.taki.cloud.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName NettyRpcClientHanlder
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 16:07
 * @Version 1.0
 */
public class NettyRpcClientHandler  extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(NettyRpcClientHandler.class);

    private static  final long GET_RPC_RESPONSE_SLEEP_INTERVAL = 5;


    private ConcurrentHashMap<String, RpcResponse> rpcResponses = new ConcurrentHashMap<>();

    private  long timeout;

    public NettyRpcClientHandler(long timeout) {
        this.timeout = timeout;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = (RpcResponse) msg;

        if (response.getTimeout()){
            logger.error("netty rpc client receives the  response  timeout:{}",response);
        }else {
            rpcResponses.put(response.getRequestId(),response);
        }
    }


    public RpcResponse getRpcResponse(String serviceId){

        long waitStartTime = System.currentTimeMillis();

        while(rpcResponses.get(serviceId) == null) {
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
        RpcResponse rpcResponse = rpcResponses.get(serviceId);

        if (Objects.isNull(rpcResponse)){
            logger.error("get rpc response timeout.");
            throw new NettyRpcReadTimeoutException("get rpc response timeout.");
        }else {
            rpcResponses.remove(serviceId);
        }

        return rpcResponse;

    }
}
