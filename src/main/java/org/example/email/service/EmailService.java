package org.example.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.entity.UserEntity;

import java.util.List;

public interface EmailService {
    String createCode();
    MimeMessage createMail(String mail, String authCode) throws MessagingException;
    String sendSimpleMessage(String sendEmail) throws MessagingException;
    void sendCompletionNotification(ProductEntity product, List<UserEntity> participants);
    void sendChatNotification(List<UserEntity> participants, ChatMessageDto chatMessageDto, ChatRoomEntity room);
}
