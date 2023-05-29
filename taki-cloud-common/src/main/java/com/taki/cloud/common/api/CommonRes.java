package com.taki.cloud.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName CommonRes
 * @Description TODO
 * @Author Long
 * @Date 2023/5/29 20:58
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonRes<T>  implements Serializable {

    private String status;

    private  T data;

    /***
     * @description: 成功响应结果
     * @param data 返回数据
     * @return  com.taki.cloud.common.api.CommonRes<T>
     * @author Long
     * @date: 2023/5/29 21:01
     */
    public static <T> CommonRes<T> success(T data) { return   new CommonRes<>("success",data);}



   // public  static  <CommonError> CommonError<CommonError> fail(CommonError  errorData) {return   new CommonRes<>("fail",errorData);}
    /***
     * @description: 报错响应结果
     * @param data 报错信息
     * @return
     * @author Long
     * @date: 2023/5/29 21:03
     */
    public static <CommonError> CommonRes<CommonError> fail(CommonError data) {
        return new CommonRes<>("fail", data);
    }

}
