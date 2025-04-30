package org.example.chat.repository;

import lombok.AllArgsConstructor;
import org.example.chat.repository.entity.ChatMessageEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    @Override
    public ChatMessageEntity save(ChatMessageEntity chatMessage){ return chatMessageJpaRepository.save(chatMessage);}
}
