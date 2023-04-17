package org.evergreen.evergreenuaa.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.evergreen.evergreenuaa.config.PropertiesConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "evergreen.sms-provider",name = "name",havingValue = "ali")
public class SmsServiceAliSmsImpl implements SmsService{

    private final IAcsClient iAcsClient;

    private final PropertiesConfig propertiesConfig;

    @Override
    public void sendMsg(String mobile, String message) {
        val request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(propertiesConfig.getSmsProvider().getApiUrl());
        request.setSysAction("SendSms");
        request.setSysVersion("2017-05-25");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "登录验证");
        request.putQueryParameter("TemplateCode", "SMS_154950909");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" +
                message +
                "\",\"product\":\"Spring Security For Evergreen UAA\"}");
        try {
            val response = iAcsClient.getCommonResponse(request);
            log.info("短信发送结果 {}", response.getData());
        } catch (ServerException e) {
            log.error("发送短信时产生服务端异常 {}", e.getLocalizedMessage());
        } catch (ClientException e) {
            log.error("发送短信时产生客户端异常 {}", e.getLocalizedMessage());
        }
    }
}
