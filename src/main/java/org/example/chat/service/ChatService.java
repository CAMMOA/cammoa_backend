package org.example.chat.service;

import org.example.chat.dto.request.ChatMessageRequest;
import org.example.chat.dto.response.CreateChatRoomResponse;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.users.repository.entity.UserEntity;

public interface ChatService {
    void saveMessage(Long roomId, ChatMessageRequest request);
    CreateChatRoomResponse createChatRoom(Long productId, String chatRoomName);
    void joinChatRoom(Long roomId);
    void addParticipantToRoom(ChatRoomEntity chatRoom, UserEntity user);
}
