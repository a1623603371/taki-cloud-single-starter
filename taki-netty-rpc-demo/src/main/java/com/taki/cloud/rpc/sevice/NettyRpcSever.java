package com.taki.cloud.rpc.sevice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName NettyRpcSever
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 13:27
 * @Version 1.0
 */
public class NettyRpcSever {
    private static final Logger logger = LogManager.getLogger(NettyRpcSever.class);
    private static final int DEFAULT_PORT = 9889;


    private List<ServiceConfig> serviceConfigs = new CopyOnWriteArrayList<>();
    private int port ;

    public NettyRpcSever( int port) {

        this.port = port;
    }

    public void start(){

    }
}
