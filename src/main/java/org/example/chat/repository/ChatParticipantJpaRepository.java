package org.example.chat.repository;

import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatParticipantJpaRepository extends JpaRepository<ChatParticipantEntity, Long> {
    ChatParticipantEntity save(ChatParticipantEntity chatParticipant);
    List<ChatParticipantEntity> findByChatRoom(ChatRoomEntity chatRoom);
}
