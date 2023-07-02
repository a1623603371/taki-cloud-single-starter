package com.taki.cloud.rpc.reference;



import com.taki.cloud.rpc.RpcRequest;
import com.taki.cloud.rpc.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcServiceProxy {

    public static Object createProxy(ReferenceConfig referenceConfig) {
        return Proxy.newProxyInstance(
                RpcServiceProxy.class.getClassLoader(),
                new Class[]{referenceConfig.getServiceInterfaceClass()},
                new ServiceProxyInvocationHandler(referenceConfig)
        );
    }

    static class ServiceProxyInvocationHandler implements InvocationHandler {

        private ReferenceConfig referenceConfig;

        public ServiceProxyInvocationHandler(ReferenceConfig referenceConfig) {
            this.referenceConfig = referenceConfig;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            NettyRpcClient nettyRpcClient = new NettyRpcClient(referenceConfig);
            nettyRpcClient.connect();

            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            rpcRequest.setServiceInterfaceClass(referenceConfig.getServiceInterfaceClass().getName());
            rpcRequest.setMethodName(method.getName());
            rpcRequest.setParameterTypes(method.getParameterTypes());
            rpcRequest.setArgs(args);

            RpcResponse rpcResponse = nettyRpcClient.remoteCall(rpcRequest);

            return rpcResponse.getResult();
        }
    }

}
