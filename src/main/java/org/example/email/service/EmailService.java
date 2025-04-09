package org.example.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.email.dto.SendEmailResponse;

public interface EmailService {
    String createCode();
    MimeMessage createMail(String mail, String authCode) throws MessagingException;
    SendEmailResponse sendSimpleMessage(String sendEmail) throws MessagingException;
}
