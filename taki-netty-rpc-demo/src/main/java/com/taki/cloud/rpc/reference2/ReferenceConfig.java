package com.taki.cloud.rpc.reference2;

/**
 * @ClassName ReferenceConfig
 * @Description TODO
 * @Author Long
 * @Date 2023/7/2 15:59
 * @Version 1.0
 */
public class ReferenceConfig {

    private static  final long DEFAULT_TIMEOUT = 5000;

    private  static  final String DEFAULT_SERVICE_HOST = "127.0.0.1";


    private  static  final int DEFAULT_SERVICE_POST = 9001;

    private Class serviceInterfaceClass;

    private String serviceHost;

    private int servicePort;

    private long timeout;


    public ReferenceConfig(Class serviceInterfaceClass) {
        this(serviceInterfaceClass,DEFAULT_SERVICE_HOST,DEFAULT_SERVICE_POST,DEFAULT_TIMEOUT);
    }

    public ReferenceConfig(Class serviceInterfaceClass, String serviceHost) {
        this(serviceInterfaceClass,serviceHost,DEFAULT_SERVICE_POST,DEFAULT_TIMEOUT);
    }

    public ReferenceConfig(Class serviceInterfaceClass, String serviceHost, int servicePort) {
            this(serviceInterfaceClass,serviceHost,servicePort,DEFAULT_TIMEOUT);
    }

    public ReferenceConfig(Class serviceInterfaceClass, String serviceHost, int servicePort, long timeout) {
        this.serviceInterfaceClass = serviceInterfaceClass;
        this.serviceHost = serviceHost;
        this.servicePort = servicePort;
        this.timeout = timeout;
    }

    public Class getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public void setServiceInterfaceClass(Class serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
