package com.taki.cloud.rpc;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName HessianSerialization
 * @Description TODO
 * @Author Long
 * @Date 2023/7/1 18:21
 * @Version 1.0
 */
public class HessianSerialization {


    public static byte[] serialize(Object object) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        hessianOutput.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }


    public static Object deserialize(byte[] bytes, Class clazz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        HessianInput hessianInput = new HessianInput(byteArrayInputStream);


        Object object = hessianInput.readObject(clazz);

        return object;

    }
}
