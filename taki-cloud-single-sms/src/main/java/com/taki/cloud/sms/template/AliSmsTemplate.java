package com.taki.cloud.sms.template;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.taki.cloud.common.utis.JsonUtil;
import com.taki.cloud.common.utis.StringUtil;
import com.taki.cloud.sms.entity.SmsData;
import com.taki.cloud.sms.entity.SmsResponse;
import com.taki.cloud.sms.properties.SmsProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName AliSmsTemplate
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 19:27
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class AliSmsTemplate implements SmsTemplate{

    private static final String OK = "ok";


    private final SmsProperties smsProperties;

    private final Client client;

    @Override
    public SmsResponse sendMessage(SmsData smsData, List<String> phones) {
        SendSmsRequest sendSmsRequest = new  SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(StringUtil.join(phones));
        sendSmsRequest.setSignName(smsProperties.getSignName());
        sendSmsRequest.setTemplateCode(smsProperties.getTemplateId());
        sendSmsRequest.setTemplateParam(JsonUtil.toJson(smsData.getParams()));
        try {
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            return new SmsResponse(sendSmsResponse.getBody().getCode().equalsIgnoreCase(OK),sendSmsResponse.getBody().getCode(),sendSmsResponse.getBody().getMessage());
        }catch (Exception e) {
        log.error("sendMessage fail：{}",e);
            return new SmsResponse(Boolean.FALSE,"FAIL",e.getMessage());
        }

    }
}
