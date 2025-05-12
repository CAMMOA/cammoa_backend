package org.example.chat.service;

import org.example.chat.dto.ChatMessageDto;

import java.util.List;

public interface ChatService {
    void saveMessage(Long roomId, ChatMessageDto request);
    List<ChatMessageDto> getChatHistory(Long roomId);
    boolean isRoomParticipant(String email, Long roomId);
}
