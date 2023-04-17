package org.evergreen.evergreenuaa.service;

import cn.leancloud.sms.AVSMS;
import cn.leancloud.sms.AVSMSOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
@ConditionalOnProperty(prefix = "evergreen.sms-provider", name = "name", havingValue = "lean-cloud")
public class SmsServiceLeanCloudSmsImpl implements SmsService {

    @Override
    public void sendMsg(String mobile, String message) {
        val option = new AVSMSOption();
        option.setTtl(10);
        option.setApplicationName("Spring Security Evergreen UAA");
        option.setOperation("两步验证");
        option.setTemplateName("登录验证");
        option.setSignatureName("慕课网");
        option.setType(AVSMS.TYPE.TEXT_SMS);
        Map<String, Object> envMap = new HashMap<>();
        envMap.put("smsCode", message);
        option.setEnvMap(envMap);
        AVSMS.requestSMSCodeInBackground(mobile, option)
            .take(1)
            .subscribe(
                (res) -> log.info("短信发送成功 {}", res),
                (err) -> log.error("发送短信时产生服务端异常 {}", err.getLocalizedMessage())
            );
    }
}
