package org.example.chat.repository;

import lombok.AllArgsConstructor;
import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ChatParticipantRepositoryImpl implements ChatParticipantRepository {

    private final ChatParticipantJpaRepository chatParticipantJpaRepository;

    @Override
    public ChatParticipantEntity save(ChatParticipantEntity chatParticipant){return chatParticipantJpaRepository.save(chatParticipant);}

    @Override
    public List<ChatParticipantEntity> findByChatRoom(ChatRoomEntity chatRoom){return chatParticipantJpaRepository.findByChatRoom(chatRoom);}
}
