package org.example.chat.repository;

import org.example.chat.repository.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageEntity, Long> {
    ChatMessageEntity save(ChatMessageEntity chatMessage);
}
