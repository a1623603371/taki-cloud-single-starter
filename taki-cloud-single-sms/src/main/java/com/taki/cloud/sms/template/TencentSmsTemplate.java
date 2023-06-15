package com.taki.cloud.sms.template;


import cn.hutool.http.HttpStatus;
import com.taki.cloud.sms.entity.SmsData;
import com.taki.cloud.sms.entity.SmsResponse;
import com.taki.cloud.sms.properties.SmsProperties;
import com.tencentcloudapi.common.Credential;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName TencentSmsTemplate
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 19:40
 * @Version 1.0
 */
@AllArgsConstructor
public class TencentSmsTemplate implements SmsTemplate{


        private final SmsProperties smsProperties;

    @Override
    public SmsResponse sendMessage(SmsData smsData, List<String> phones) {

        try {
            Credential cred = new Credential(smsProperties.getAccessKey(),smsProperties.getSecretKey());
            SmsClient client = new SmsClient(cred,"ap-guangzhou");
            SendSmsRequest request = new SendSmsRequest();

            request.setSmsSdkAppId(smsProperties.getSdkAppId());
            request.setSignName(smsProperties.getSignName());
            request.setTemplateId(smsProperties.getTemplateId());
            request.setPhoneNumberSet((String[]) phones.toArray());
            Collection<String> values = smsData.getParams().values();
            request.setTemplateParamSet((String[]) values.toArray());

            SendSmsResponse res = client.SendSms(request);

            return new SmsResponse(true,"200",res.toString());

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();

            return new SmsResponse(Boolean.FALSE,"500",e.getMessage());
        }



    }
}
