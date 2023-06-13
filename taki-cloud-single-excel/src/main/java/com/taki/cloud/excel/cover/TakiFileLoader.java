package com.taki.cloud.excel.cover;

import cn.afterturn.easypoi.cache.manager.IFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @ClassName EasyPoiAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 14:27
 * @Version 1.0
 */
@Slf4j
public class TakiFileLoader implements IFileLoader {
    @Override
    public byte[] getFile(String url) {

        InputStream files = null;
        ByteArrayOutputStream baos = null;

        try {

        // 判断是否网络地址
            if (url.startsWith("http")){
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setConnectTimeout(30);
                urlConnection.setReadTimeout(60);
                urlConnection.setDoInput(true);
                files = urlConnection.getInputStream();
            }else{
                // 先用绝对路径查询，在查询相对路径
                try {
                    files = new FileInputStream(url);
                }catch (FileNotFoundException e){
                    // 获取项目文件
                    files = TakiFileLoader.class.getClassLoader().getResourceAsStream(url);

                    if (files == null){
                        files = TakiFileLoader.class.getResourceAsStream(url);
                    }
                }
            }
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];

            int len;
            while ((len = files.read(buffer)) > -1){
                baos.write(buffer,0,len);
            }
            baos.flush();
            return baos.toByteArray();

        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            IOUtils.closeQuietly(files);
            IOUtils.closeQuietly(baos);
        }

        log.error("{},这个路径文件没有找到，请查询",files);
        return new byte[0];
    }


}
