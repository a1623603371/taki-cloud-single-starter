package com.taki.cloud.rpc.reference2;

/**
 * @ClassName NettyRpcReadTimeoutException
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 16:19
 * @Version 1.0
 */
public class NettyRpcReadTimeoutException extends RuntimeException{

    public NettyRpcReadTimeoutException(String message) {
        super(message);
    }
}
