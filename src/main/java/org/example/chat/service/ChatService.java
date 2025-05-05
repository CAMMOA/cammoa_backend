package org.example.chat.service;

import org.example.chat.dto.response.CreateChatRoomResponse;
import org.example.chat.dto.request.ChatMessageRequest;
import org.example.chat.dto.response.GetChatRoomsResponse;

import java.util.List;

public interface ChatService {
    void saveMessage(Long roomId, ChatMessageRequest request);
    CreateChatRoomResponse createChatRoom(Long productId, String chatRoomName);
    List<GetChatRoomsResponse> getChatRooms();
}
