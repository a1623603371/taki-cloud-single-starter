package com.taki.cloud.rpc.sevice;

/**
 * @ClassName TestServiceImpl
 * @Description TODO
 * @Author Long
 * @Date 2023/7/1 19:40
 * @Version 1.0
 */
public class TestServiceImpl implements  TestService{
    @Override
    public String sayHello(String name) {
        return "hello" + name;
    }
}
