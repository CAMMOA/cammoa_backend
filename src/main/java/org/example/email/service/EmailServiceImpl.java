package org.example.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.email.dto.response.SendEmailResponse;
import org.example.exception.impl.AuthException;
import org.example.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender mailSender;
    private final RedisUtil redis;

    //인증코드 생성
    public String createCode() {
        SecureRandom secureRandom = new SecureRandom(); // Use SecureRandom for better randomness
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) { // Generate a 6-character authentication code
            int index = secureRandom.nextInt(2); // Randomly choose between letter and digit

            if (index == 0) {
                // Append a random uppercase letter (A-Z)
                key.append((char) ('A' + secureRandom.nextInt(26)));
            } else {
                // Append a random digit (0-9)
                key.append(secureRandom.nextInt(10));
            }
        }
        return key.toString();
    }

    //이메일 생성
    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    //이메일 전송
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String authCode = createCode();

        MimeMessage message = createMail(sendEmail, authCode);
        try{
            javaMailSender.send(message);

            return authCode;

        } catch (MailException e){
            throw new AuthException(ErrorResponseEnum.EMAIL_SEND_FAILED);
        }
    }
}
