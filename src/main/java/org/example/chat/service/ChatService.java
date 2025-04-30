package org.example.chat.service;

import org.example.chat.dto.response.CreateChatRoomResponse;
import org.example.chat.dto.request.ChatMessageRequest;

public interface ChatService {
    void saveMessage(Long roomId, ChatMessageRequest request);
    CreateChatRoomResponse createChatRoom(Long productId, String chatRoomName);
}
