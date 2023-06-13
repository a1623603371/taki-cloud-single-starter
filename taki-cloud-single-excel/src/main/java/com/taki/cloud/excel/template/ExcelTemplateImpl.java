package com.taki.cloud.excel.template;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;


import cn.hutool.http.server.HttpServerResponse;
import com.taki.cloud.excel.enums.ExcelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @ClassName ExcelTemplateImpl
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 13:19
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ExcelTemplateImpl implements ExcelTemplate{


    private String importExcelStorePath;


    @Override
    public void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) throws IOException {
        ExportParams exportParams = new ExportParams(title,sheetName, ExcelType.HSSF);

        exportParams.setCreateHeadRows(isCreateHeader);

        defaultExport(list,pojoClass,fileName,response,exportParams);

    }

    private void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) throws IOException {

        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        downLoadExcel(fileName,response,workbook);

    }

    private void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) throws IOException {

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type","application/vnd.ms-excel");
            response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName+"." + ExcelTypeEnum.XLSX.getValue(),"UTF-8"));
            workbook.write(response.getOutputStream());
        }catch (Exception e){
        throw new IOException(e);
        }

    }

    @Override
    public void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) throws IOException {

        defaultExport(list,pojoClass,fileName,response,new ExportParams(title,sheetName,ExcelType.HSSF));

    }

    @Override
    public void exportExcel(List<Map<String,Object>> list, Class<?> pojoClass, String fileName, ExportParams params, HttpServletResponse response) throws IOException {
        defaultExport(list,pojoClass,fileName,response,params);
    }

    @Override
    public <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {

        if (StringUtils.isEmpty(filePath)){
            return null;
        }

        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(true);
        params.setSaveUrl("/excel/");

        try {
            return ExcelImportUtil.importExcel(new File(filePath),pojoClass,params);
        }catch (NoSuchElementException e){
           throw new IOException("模板不能为空");
        }catch (Exception e){
            throw new IOException(e.getMessage());
        }

    }

    @Override
    public <T> List<T> importExcel(MultipartFile execlFile, Class<T> pojoClass) throws IOException {
        return importExcel(execlFile,1,1,pojoClass);
    }

    @Override
    public <T> List<T> importExcel(MultipartFile execlFile, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        return importExcel(execlFile,titleRows,headerRows,false,pojoClass);
    }

    @Override
    public <T> List<T> importExcel(MultipartFile execlFile, Integer titleRows, Integer headerRows, Boolean needVerify, Class<T> pojoClass) throws IOException {
        if (ObjectUtils.isEmpty(execlFile)){
            return null;
        }
        try {
            return importExcel(execlFile.getInputStream(), titleRows,headerRows,needVerify,pojoClass);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    @Override
    public <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, Boolean needVerify, Class<T> pojoClass) throws IOException {

        if (ObjectUtils.isEmpty(inputStream)){
            return null;
        }

        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSaveUrl(importExcelStorePath);
        params.setNeedSave(true);
        params.setNeedVerify(needVerify);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("excel文件不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
