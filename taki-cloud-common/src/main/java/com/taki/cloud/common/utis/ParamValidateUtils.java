package com.taki.cloud.common.utis;

import org.springframework.validation.BindingResult;

/**
 * @ClassName ParamValidateUtils
 * @Description 参数效验工具类
 * @Author Long
 * @Date 2023/6/12 16:03
 * @Version 1.0
 */
public class ParamValidateUtils {

    /*** 
     * @description:  获取效验结果
     * @param bindingResult 入参绑定结果
     * @return  java.lang.String
     * @author Long
     * @date: 2023/6/12 16:06
     */ 
    public static String processErrorString(BindingResult  bindingResult){

        if(!bindingResult.hasErrors()){
            return "";
        }

        StringBuilder result = new StringBuilder();
        bindingResult.getFieldErrors().forEach(fieldError -> {

            result.append(fieldError.getDefaultMessage()).append(",");
        });

        return result.substring(0,result.length() - 1);

    }
}
