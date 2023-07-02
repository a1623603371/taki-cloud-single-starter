package com.taki.cloud.rpc.reference2;

import com.taki.cloud.rpc.sevice.TestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName NettyRpcClientTest
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 18:34
 * @Version 1.0
 */
public class NettyRpcClientTest {

    private static  final Logger logger = LogManager.getLogger(NettyRpcClientTest.class);


    public static void main(String[] args) {
        ReferenceConfig  referenceConfig = new ReferenceConfig(TestService.class);
        TestService testService = (TestService) RpcServiceProxy.createProxy(referenceConfig);

        String result = testService.sayHello("taki");

        logger.info("rpc call finished:{}",result);
    }
}
