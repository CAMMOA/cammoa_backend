package org.example.email.service;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.example.products.repository.entity.ProductEntity;
import org.example.redis.RedisService;
import org.example.users.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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

    // 이메일 생성
    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");

        MimeMultipart multipart = new MimeMultipart("related");
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        String htmlContent = createAuthHtmlContent(authCode);
        htmlBodyPart.setContent(htmlContent, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlBodyPart);

        addLogoToMultipart(multipart);
        message.setContent(multipart);
        return message;
    }

    // 공통 베이스 HTML 구조
    private String createBaseHtml(String title, String content) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='ko'><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { background: #f3f4f6; margin:0; padding:0; font-family: Arial, sans-serif; }");
        html.append(".mail-wrap { max-width: 520px; margin:40px auto; background:#fff; border-radius:10px; box-shadow:0 2px 10px rgba(0,0,0,0.07); overflow:hidden; }");
        html.append(".mail-header { background: #e6eef7; padding:32px 0 16px 0; text-align:center; }");
        html.append(".mail-header img { max-width: 180px; height:auto; }");
        html.append(".mail-content { padding: 36px 36px 24px 36px; }");
        html.append(".mail-title { font-size: 22px; font-weight: bold; color: #222; margin-bottom: 18px; text-align:left; }");
        html.append(".mail-desc { color: #444; font-size: 16px; margin-bottom: 18px; line-height: 1.7; text-align:left; }");
        html.append(".highlight-box { color: #232f3e; background: #f1f2f4; font-size: 18px; font-weight: bold; border-radius: 8px; padding: 18px; margin-bottom: 18px; border: 1px solid #e3e5e8; text-align: center; }");
        html.append(".auth-code { color: #232f3e; background: #f1f2f4; font-size: 36px; font-weight: bold; letter-spacing: 4px; border-radius: 8px; padding: 18px 0; text-align: center; margin-bottom: 12px; border: 1px solid #e3e5e8; }");
        html.append(".expire-info { color: #888; font-size: 14px; text-align: left; margin-bottom: 18px; }");
        html.append(".mail-warning { color: #888; font-size: 13px; background: #f7f7f9; border-radius: 6px; padding: 14px 12px; margin-bottom: 18px; text-align: left; }");
        html.append(".mail-footer { color: #aaa; font-size: 12px; text-align: left; padding: 18px 20px 12px 20px; border-top: 1px solid #eee; background: #fafbfc; }");
        html.append(".product-info { background: #f7f7f9; border-radius: 6px; padding: 16px; margin-bottom: 18px; }");
        html.append(".product-info p { margin: 8px 0; color: #444; }");
        html.append(".message-box { background: #e8f4f8; border-left: 4px solid #4285f4; padding: 16px; margin-bottom: 18px; border-radius: 0 6px 6px 0; }");
        html.append("</style></head><body>");
        html.append("<div class='mail-wrap'>");
        html.append("<div class='mail-header'><img src='cid:logo' alt='CAMMOA 로고'></div>");
        html.append("<div class='mail-content'>");
        html.append("<div class='mail-title'>").append(title).append("</div>");
        html.append(content);
        html.append("</div>");
        html.append("<div class='mail-footer'>&copy; 2025 CAMMOA. All rights reserved.</div>");
        html.append("</div></body></html>");
        return html.toString();
    }

    //이메일 전송
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        if (!sendEmail.toLowerCase().endsWith("@hufs.ac.kr")) {
            throw new AuthException(ErrorResponseEnum.INVALID_EMAIL); // 사용자 정의 예외 처리
        }

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

    // 공동구매 완료 알림 이메일
    @Override
    public void sendCompletionNotification(ProductEntity product, List<UserEntity> participants) {
        for (UserEntity user : participants) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                message.setFrom(senderEmail);
                message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
                message.setSubject("[모집 완료] '" + product.getTitle() + "' 공동구매 모집이 완료되었습니다!");

                // MimeMultipart 생성 (related 타입으로 설정)
                MimeMultipart multipart = new MimeMultipart("related");

                // HTML 콘텐츠 부분 생성
                MimeBodyPart htmlBodyPart = new MimeBodyPart();
                String htmlContent = createCompletionHtmlContent(product);
                htmlBodyPart.setContent(htmlContent, "text/html; charset=UTF-8");
                multipart.addBodyPart(htmlBodyPart);

                addLogoToMultipart(multipart);
                message.setContent(multipart);
                javaMailSender.send(message);

            } catch (MessagingException e) {
                System.err.println("이메일 전송 실패: " + user.getEmail());
            }
        }
    }

    // 채팅 알림 이메일
    @Async
    @Override
    public void sendChatNotification(List<UserEntity> participants, ChatMessageDto chatMessageDto, ChatRoomEntity room) {
        for (UserEntity user : participants) {
            try {
                MimeMessage email = javaMailSender.createMimeMessage();
                email.setFrom(senderEmail);
                email.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
                email.setSubject("[채팅 알림] '" + room.getChatRoomName() + "'에 새 메시지 도착");

                MimeMultipart multipart = new MimeMultipart("related");
                MimeBodyPart htmlBodyPart = new MimeBodyPart();
                String htmlContent = createChatHtmlContent(chatMessageDto, room);
                htmlBodyPart.setContent(htmlContent, "text/html; charset=UTF-8");
                multipart.addBodyPart(htmlBodyPart);

                addLogoToMultipart(multipart);
                email.setContent(multipart);
                javaMailSender.send(email);
            } catch (MessagingException e) {
                throw new AuthException(ErrorResponseEnum.EMAIL_SEND_FAILED);
            }
        }
    }

    // 공통 로고 추가 메서드
    private void addLogoToMultipart(MimeMultipart multipart) throws MessagingException {
        try {
            MimeBodyPart imagePart = new MimeBodyPart();
            ClassPathResource logoResource = new ClassPathResource("images/logo.png");
            imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(logoResource.getInputStream(), "image/png")));
            imagePart.setHeader("Content-ID", "<logo>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imagePart);
        } catch (Exception e) {
            System.err.println("로고 이미지 로드 실패: " + e.getMessage());
        }
    }

    // 인증 이메일 HTML
    private String createAuthHtmlContent(String authCode) {
        StringBuilder content = new StringBuilder();
        content.append("<div class='mail-desc'>");
        content.append("CAMMOA를 이용해 주셔서 감사합니다!<br>");
        content.append("본인 확인을 위해 아래 인증 코드를 입력해 주세요.<br>");
        content.append("만약 본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.");
        content.append("</div>");
        content.append("<div class='auth-code'>").append(authCode).append("</div>");
        content.append("<div class='expire-info'>이 코드는 전송 후 10분이 경과하면 만료됩니다.</div>");
        content.append("<div class='mail-warning'>");
        content.append("CAMMOA는 절대 암호, 신용카드, 계좌번호 등의 정보를 요청하지 않습니다.");
        content.append("</div>");
        return createBaseHtml("이메일 인증", content.toString());
    }

    // 공동구매 완료 HTML
    private String createCompletionHtmlContent(ProductEntity product) {
        StringBuilder content = new StringBuilder();
        content.append("<div class='mail-desc'>");
        content.append("참여하신 공동구매 모집이 성공적으로 완료되었습니다!<br>");
        content.append("이제 구매 진행 단계로 넘어가며, 추가 안내는 별도로 연락드리겠습니다.");
        content.append("</div>");
        content.append("<div class='highlight-box'>모집 완료</div>");
        content.append("<div class='product-info'>");
        content.append("<p><strong>상품명:</strong> ").append(product.getTitle()).append("</p>");
        content.append("<p><strong>마감일:</strong> ").append(product.getDeadline()).append("</p>");
        content.append("</div>");
        content.append("<div class='mail-warning'>");
        content.append("추가 문의사항이 있으시면 CAMMOA 고객센터로 연락해 주세요.");
        content.append("</div>");
        return createBaseHtml("공동구매 모집 완료", content.toString());
    }

    // 채팅 알림 HTML
    private String createChatHtmlContent(ChatMessageDto chatMessageDto, ChatRoomEntity room) {
        StringBuilder content = new StringBuilder();
        content.append("<div class='mail-desc'>");
        content.append("참여 중인 채팅방에 새로운 메시지가 도착했습니다.<br>");
        content.append("CAMMOA 앱에서 확인해 보세요!");
        content.append("</div>");
        content.append("<div class='highlight-box'>").append(room.getChatRoomName()).append("</div>");
        content.append("<div class='message-box'>");
        content.append("<p><strong>").append(chatMessageDto.getSenderNickname()).append("님:</strong></p>");
        content.append("<p>").append(chatMessageDto.getMessage()).append("</p>");
        content.append("</div>");
        return createBaseHtml("새 메시지 알림", content.toString());
    }
}
