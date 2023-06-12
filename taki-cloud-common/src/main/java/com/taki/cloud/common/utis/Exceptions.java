package com.taki.cloud.common.utis;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @ClassName Exceptions
 * @Description 异常处理工具类
 * @Author Long
 * @Date 2023/6/12 12:52
 * @Version 1.0
 */
public class Exceptions {

    /*** 
     * @description:  将checkedException 转换为 uncheckedExcption
     * @param e
     * @return  java.lang.RuntimeException
     * @author Long
     * @date: 2023/6/12 12:54
     */ 
    public static RuntimeException unchecked(Throwable e){

        if (e instanceof  Error){
             throw (Error)e;
        }else if (e  instanceof   IllegalArgumentException ||
                  e   instanceof  IllegalAccessException  ||
                  e   instanceof   NoSuchMethodException ) {
            return  new IllegalArgumentException(e);
        }else  if (e instanceof InvocationTargetException ){
            return  Exceptions.runtime(((InvocationTargetException) e).getTargetException());
        }else if (e instanceof RuntimeException){
            return (RuntimeException) e;
        }else if (e instanceof InternalException){
            Thread.currentThread().interrupt();
        }
        return  Exceptions.runtime(e);

    }
    
    /*** 
     * @description: 不采用 RuntimeException 包装 直接抛出 使异常更加精准
     * @param throwable
     * @return  java.lang.RuntimeException
     * @author Long
     * @date: 2023/6/12 12:57
     */ 
    private static<T extends  Throwable  >  T runtime(Throwable throwable) throws T {

        throw (T)throwable;
    }

    /*** 
     * @description:  代理异常解包
     * @param waapped
     * @return  java.lang.Throwable
     * @author Long
     * @date: 2023/6/12 13:57
     */ 
    public static Throwable unwrap(Throwable  waapped){

        Throwable unwarapped = waapped;

        while (true){
            if (unwarapped  instanceof   InvocationTargetException){
                    unwarapped = ((InvocationTargetException) unwarapped).getTargetException();
            }else if (unwarapped instanceof UndeclaredThrowableException){
                    unwarapped = ((UndeclaredThrowableException) unwarapped).getUndeclaredThrowable();
            }else {
                return  unwarapped;
            }
        }
    }

    /*** 
     * @description:  将Error转为String
     * @param throwable
     * @return  java.lang.String
     * @author Long
     * @date: 2023/6/12 14:00
     */ 
    public static String getStackTraceAsString(Throwable throwable){
        FastStringPrintWriter printWriter = new FastStringPrintWriter(512);
        throwable.printStackTrace();
        return printWriter.toString();
    }

}
