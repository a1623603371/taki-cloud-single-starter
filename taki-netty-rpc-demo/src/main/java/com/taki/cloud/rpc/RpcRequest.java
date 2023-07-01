package com.taki.cloud.rpc;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @ClassName RpcRequest
 * @Description TODO
 * @Author Long
 * @Date 2023/7/1 18:33
 * @Version 1.0
 */
public class RpcRequest implements Serializable {


    private static final long serialVersionUID = 7797125483940444588L;


    private String requestId;

    private String serviceInterfaceClass;

    private String methodName;

    private Class[] parameterTypes;

    private Object[] args;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public void setServiceInterfaceClass(String serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] paramterTypes) {
        this.parameterTypes = paramterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", serviceInterfaceClass='" + serviceInterfaceClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
