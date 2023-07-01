package com.taki.cloud.rpc.sevice;

import com.taki.cloud.rpc.RpcRequest;
import com.taki.cloud.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ClassName NettyRpcServerHander
 * @Description TODO
 * @Author Long
 * @Date 2023/7/1 19:02
 * @Version 1.0
 */
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private static  final Logger logger  = LogManager.getLogger(NettyRpcServerHandler.class);

    private ConcurrentHashMap<String,ServiceConfig> serviceConfigMap = new ConcurrentHashMap<>();


    public NettyRpcServerHandler(List<ServiceConfig>serviceConfigs) {
            serviceConfigs.forEach(serviceConfig -> {
            String serviceInterfaceClass = serviceConfig.getServiceInterfaceClass().getName();
            serviceConfigMap.put(serviceInterfaceClass,serviceConfig);
            });
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        RpcRequest rpcRequest  = (RpcRequest) msg;
        logger.info("netty rpc server  receives  the request :" + rpcRequest);

        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());

        try {
            // 此时我们要实现一个什么东西呢？我们需要根据人家指定的class，获取到这个class
            // 根据人家的方法调用，去通过反射，构建这个class对象实例
            // 接着通过放射获取到这个class指定方法入参类型的method，反射调用，传入方法，拿到返回值
            ServiceConfig serviceConfig = serviceConfigMap.get(rpcRequest.getServiceInterfaceClass());
            Class clazz = serviceConfig.getServiceClass();
            Object instance = clazz.newInstance();
            Method method = clazz.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());

            Object result = method.invoke(instance,rpcRequest.getArgs());
            response.setResult(result);

            response.setSuccess(RpcResponse.SUCCESS);
        }catch (Exception e){
           logger.error("netty rpc  server failed to  response  the request.",e);
           response.setSuccess(RpcResponse.FAILURE);
           response.setException(e);
        }

        ctx.write(response);
        ctx.flush();
        logger.info("send  rpc  response to  client :" +  rpcRequest);

    }
}
