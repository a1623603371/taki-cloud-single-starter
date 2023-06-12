package com.taki.cloud.common.utis;

import cn.hutool.core.io.FastStringWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @ClassName FastStringPrintWriter
 * @Description 快速的 PrintWriter ，用于处理异常信息转为字符串
 * @Author Long
 * @Date 2023/6/12 14:01
 * @Version 1.0
 */
public class FastStringPrintWriter extends PrintWriter {

    private final FastStringWriter writer;


    public FastStringPrintWriter(){
        this(256);
    }

    public FastStringPrintWriter(int initialSize) {
        super(new FastStringPrintWriter(initialSize));
        this.writer = (FastStringWriter) out;
    }

    @Override
    public void print(Object x) {
        writer.write(String.valueOf(x));
        writer.write("\n");
    }

    @Override
    public String toString() {
        return writer.toString();
    }
}
