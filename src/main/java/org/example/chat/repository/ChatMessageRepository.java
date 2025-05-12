package org.example.chat.repository;

import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatRoomEntity;

import java.util.List;

public interface ChatMessageRepository {
    ChatMessageEntity save(ChatMessageEntity chatMessage);
    List<ChatMessageEntity> findByChatRoomOrderByCreatedTimeAsc(ChatRoomEntity chatRoom);
}
