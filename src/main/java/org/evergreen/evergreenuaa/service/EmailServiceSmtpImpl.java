package org.evergreen.evergreenuaa.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "evergreen.email-provider", name = "name", havingValue = "smtp")
@RequiredArgsConstructor
@Service
public class EmailServiceSmtpImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String email, String message) {
        val msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setFrom("307588275@qq.com");
        msg.setSubject("Spring Security Evergreen UAA 登录验证码");
        msg.setText("验证码为:" + message);
        javaMailSender.send(msg);
    }
}
