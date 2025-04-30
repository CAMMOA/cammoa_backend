package org.example.chat.repository;

import org.example.chat.repository.entity.ChatMessageEntity;

public interface ChatMessageRepository {
    ChatMessageEntity save(ChatMessageEntity chatMessage);
}
