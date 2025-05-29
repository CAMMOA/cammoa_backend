package org.example.chat.repository;

import lombok.AllArgsConstructor;
import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    @Override
    public ChatMessageEntity save(ChatMessageEntity chatMessage){ return chatMessageJpaRepository.save(chatMessage);}

    @Override
    public List<ChatMessageEntity> findByChatRoomOrderByCreatedTimeAsc(ChatRoomEntity chatRoom){ return chatMessageJpaRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom); }

    @Override
    public ChatMessageEntity findTopByChatRoomOrderByCreatedTimeDesc(ChatRoomEntity chatRoom){ return chatMessageJpaRepository.findTopByChatRoomOrderByCreatedTimeDesc(chatRoom); }
}
