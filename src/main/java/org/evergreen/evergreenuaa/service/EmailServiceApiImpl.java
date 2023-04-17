package org.evergreen.evergreenuaa.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@ConditionalOnProperty(prefix = "evergreen.email-provider", name = "name", havingValue = "api")
@RequiredArgsConstructor
@Service
public class EmailServiceApiImpl implements EmailService {

    private final SendGrid sendGrid;

    @Override
    public void sendEmail(String email, String msg) {
        val from = new Email("307588275@qq.com");
        val subject = "Spring Security Evergreen UAA 登录验证码";
        val to = new Email(email);
        val content = new Content("text/plain", "验证码为:" + msg);
        val mail = new Mail(from, subject, to, content);
        val request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() == 202) {
                log.info("邮件发送成功");
            } else {
                log.error(response.getBody());
            }
        } catch (IOException e) {
            log.error("请求发生异常 {}", e.getLocalizedMessage());
        }
    }
}
