package com.taki.cloud.rpc.sevice;

/**
 * @ClassName ServiceConfig
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 13:30
 * @Version 1.0
 */
public class ServiceConfig {

    private String configName;

    private Class serviceInterfaceClass;
    private Class serviceClass;

    public ServiceConfig(String configName, Class serviceInterfaceClass, Class serviceClass) {
        this.configName = configName;
        this.serviceInterfaceClass = serviceInterfaceClass;
        this.serviceClass = serviceClass;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Class getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public void setServiceInterfaceClass(Class serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "configName='" + configName + '\'' +
                ", serviceInterfaceClass=" + serviceInterfaceClass +
                ", serviceClass=" + serviceClass +
                '}';
    }
}
