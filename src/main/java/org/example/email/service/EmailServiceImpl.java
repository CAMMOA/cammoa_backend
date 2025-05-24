package org.example.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.example.products.repository.entity.ProductEntity;
import org.example.redis.RedisService;
import org.example.users.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Value("${MAIL_USERNAME}")
    private String senderEmail;

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
        if(redisService.existData(sendEmail)){
            redisService.deleteData(sendEmail);
        }

        String authCode = createCode();

        MimeMessage message = createMail(sendEmail, authCode);
        try{
            javaMailSender.send(message);

            return authCode;

        } catch (MailException e){
            throw new AuthException(ErrorResponseEnum.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public void sendCompletionNotification(ProductEntity product, List<UserEntity> participants) {
        for (UserEntity user : participants) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                message.setFrom(senderEmail);
                message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
                message.setSubject("[모집 완료] '" + product.getTitle() + "' 공동구매 모집이 완료되었습니다!");

                String body = "<h3>참여하신 공동구매가 모집 완료되었습니다.</h3>"
                        + "<p>상품명: <strong>" + product.getTitle() + "</strong></p>"
                        + "<p>마감일: " + product.getDeadline() + "</p>";

                message.setText(body, "UTF-8", "html");
                javaMailSender.send(message);
            } catch (MessagingException e) {
                System.err.println("이메일 전송 실패: " + user.getEmail());
            }
        }
    }

    @Override
    public void sendChatNotification(List<UserEntity> participants, ChatMessageDto chatMessageDto, ChatRoomEntity room){
        for(UserEntity user: participants){
            try{
                MimeMessage email = javaMailSender.createMimeMessage();
                email.setFrom(senderEmail);
                email.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
                email.setSubject("[채팅 알림] '" + room.getChatRoomName() + "'에 새 메시지 도착");

                String body = "<h3>" + chatMessageDto.getSenderNickname() + "님이 메시지를 보냈습니다.</h3>"
                        + "<p>" + chatMessageDto.getMessage() + "</p>";

                email.setText(body, "UTF-8", "html");
                javaMailSender.send(email);
            } catch (MessagingException e){
                throw new AuthException(ErrorResponseEnum.EMAIL_SEND_FAILED);
            }
        }
    }
}
