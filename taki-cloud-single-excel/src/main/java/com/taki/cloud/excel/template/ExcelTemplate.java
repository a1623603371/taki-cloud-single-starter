package com.taki.cloud.excel.template;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.http.server.HttpServerResponse;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName ExcelTemplate
 * @Description excel 操作接口
 * @Author Long
 * @Date 2023/6/12 19:56
 * @Version 1.0
 */
public interface ExcelTemplate {

    /*** 
     * @description: 导出
     * @param list 数据
     * @param title 标题
     * @param sheetName she
     * @param pojoClass pojo 类型
     * @param fileName 文件名称
     * @param isCreateHeader 是否创建表头
     * @param response 返回对象
     * @return  void
     * @author Long
     * @date: 2023/6/12 19:58
     */ 
    void exportExcel(List<?> list, String  title, String sheetName, Class<?> pojoClass, String fileName, boolean  isCreateHeader, HttpServerResponse response) throws IOException;


    /***
     * @description: 导出
     * @param list 数据
     * @param title 标题
     * @param sheetName she
     * @param pojoClass pojo 类型
     * @param fileName 文件名称
     * @param response 返回对象
     * @return  void
     * @author Long
     * @date: 2023/6/12 19:58
     */
    void exportExcel(List<?> list, String  title, String sheetName, Class<?> pojoClass, String fileName,  HttpServerResponse response) throws IOException;


    /***
     * @description: 导出
     * @param list 数据
     * @param pojoClass pojo 类型
     * @param fileName 文件名称
     * @param response 返回对象
     * @return  void
     * @author Long
     * @date: 2023/6/12 19:58
     */
    void exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams params, HttpServerResponse response) throws IOException;
}
