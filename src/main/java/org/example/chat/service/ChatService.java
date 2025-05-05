package org.example.chat.service;

import org.example.chat.dto.request.ChatMessageRequest;
import org.example.chat.dto.response.CreateChatRoomResponse;

public interface ChatService {
    void saveMessage(Long roomId, ChatMessageRequest request);
    CreateChatRoomResponse createChatRoom(Long productId, String chatRoomName);
}
