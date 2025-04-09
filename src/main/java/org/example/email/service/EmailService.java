package org.example.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.email.dto.response.SendEmailResponse;

public interface EmailService {
    String createCode();
    MimeMessage createMail(String mail, String authCode) throws MessagingException;
    String sendSimpleMessage(String sendEmail) throws MessagingException;
}
