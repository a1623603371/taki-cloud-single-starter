package com.taki.cloud.excel.template;

import cn.afterturn.easypoi.excel.entity.ExportParams;


import cn.hutool.http.server.HttpServerResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
    void exportExcel(List<?> list, String  title, String sheetName, Class<?> pojoClass, String fileName, boolean  isCreateHeader, HttpServletResponse response) throws IOException;


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
    void exportExcel(List<?> list, String  title, String sheetName, Class<?> pojoClass, String fileName,  HttpServletResponse response) throws IOException;


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
    void exportExcel(List<Map<String,Object>> list, Class<?> pojoClass, String fileName, ExportParams params, HttpServletResponse response) throws IOException;


    /***
     * @description: excel 导入
     * @param filePath 文件 路径
     * @param  titleRows 标题行
     * @param headerRows 表头行
     * @param  pojoClass pojo 类型
     * @return  void
     * @author Long
     * @date: 2023/6/13 13:08
     */
    <T> List<T> importExcel(String  filePath, Integer  titleRows, Integer headerRows, Class<T> pojoClass)throws  IOException;

    /***
     * @description: excel 导入
     * @param execlFile excel文件

     * @param  pojoClass pojo 类型
     * @return  void
     * @author Long
     * @date: 2023/6/13 13:08
     */
    <T> List<T> importExcel(MultipartFile execlFile, Class<T> pojoClass)throws  IOException;


    /***
     * @description: excel 导入
     * @param execlFile excel文件
     * @param  titleRows 标题行
     * @param headerRows 表头行
     * @param  pojoClass pojo 类型
     * @return  void
     * @author Long
     * @date: 2023/6/13 13:08
     */
    <T> List<T> importExcel(MultipartFile execlFile, Integer  titleRows, Integer headerRows, Class<T> pojoClass)throws  IOException;




    /***
     * @description: excel 导入
     * @param execlFile excel文件
     * @param  titleRows 标题行
     * @param headerRows 表头行
     * @param  needVerify 是否验证 excel 文件
     * @param  pojoClass pojo 类型
     * @return  void
     * @author Long
     * @date: 2023/6/13 13:08
     */
    <T> List<T> importExcel(MultipartFile execlFile, Integer  titleRows, Integer headerRows,Boolean needVerify, Class<T> pojoClass)throws  IOException;

    /***
     * @description: excel 导入
     * @param inputStream 文件输出流
     * @param  titleRows 标题行
     * @param headerRows 表头行
     * @param  needVerify 是否验证 excel 文件
     * @param  pojoClass pojo 类型
     * @return  void
     * @author Long
     * @date: 2023/6/13 13:08
     */
    <T> List<T> importExcel(InputStream inputStream , Integer  titleRows, Integer headerRows, Boolean needVerify, Class<T> pojoClass)throws  IOException;
}
