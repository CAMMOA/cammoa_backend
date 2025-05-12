package org.example.chat.repository;

import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageEntity, Long> {
    ChatMessageEntity save(ChatMessageEntity chatMessage);
    List<ChatMessageEntity> findByChatRoomOrderByCreatedTimeAsc(ChatRoomEntity chatRoom);
}
