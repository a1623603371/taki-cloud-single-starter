package com.taki.cloud.sms.template;

import com.taki.cloud.sms.entity.SmsData;
import com.taki.cloud.sms.entity.SmsResponse;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName SmsTemplate
 * @Description 短信发送封装接口
 * @Author Long
 * @Date 2023/6/14 19:20
 * @Version 1.0
 */
public interface SmsTemplate {
    /***
     * @description: 发送短信
     * @param smsData 短信内容
     * @param phone 手机号
     * @return  boolean
     * @author Long
     * @date: 2023/6/14 19:24
     */
 default boolean send(SmsData  smsData ,String phone){

     if (StringUtils.isEmpty(phone)){
            return  Boolean.FALSE;
     }

     return sendMulti(smsData, Collections.singletonList(phone));
 }

 /*** 
  * @description: 发送短信
  * @param smsData 短信内容
  * @param phones
  * @return  boolean
  * @author Long
  * @date: 2023/6/14 19:24
  */ 
    default  Boolean sendMulti(SmsData smsData, List<String> phones){

        SmsResponse response = sendMessage(smsData,phones);

        return response.getSuccess();
    }

    SmsResponse sendMessage(SmsData smsData, List<String> phones);

}
