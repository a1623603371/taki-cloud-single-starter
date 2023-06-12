package com.taki.cloud.es.exception;

/**
 * @ClassName EsException
 * @Description TODO
 * @Author Long
 * @Date 2023/6/12 19:16
 * @Version 1.0
 */
public class EsException  extends RuntimeException{

    public EsException(String message) {
        super(message);
    }
}
