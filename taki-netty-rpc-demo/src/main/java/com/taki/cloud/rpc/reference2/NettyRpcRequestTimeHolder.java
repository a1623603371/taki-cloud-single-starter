package com.taki.cloud.rpc.reference2;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName NettyRpcRequestTimeHolder
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 16:31
 * @Version 1.0
 */
public class NettyRpcRequestTimeHolder {

    private static ConcurrentHashMap<String,Long> requestTimes = new ConcurrentHashMap<>();


    public static void put(String requestId,long requestTime){
        requestTimes.put(requestId,requestTime);
    }

    public static Long    get(String requestId){
        return requestTimes.get(requestId);
    }

    public static  void remove(String requestId){
        requestTimes.remove(requestId);
    }
}
