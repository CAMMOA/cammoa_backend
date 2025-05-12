package org.example.chat.repository;

import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.users.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantJpaRepository extends JpaRepository<ChatParticipantEntity, Long> {
    ChatParticipantEntity save(ChatParticipantEntity chatParticipant);
    List<ChatParticipantEntity> findByChatRoom(ChatRoomEntity chatRoom);
    Optional<ChatParticipantEntity> findByChatRoomAndUser(ChatRoomEntity chatRoom, UserEntity user);
    List<ChatParticipantEntity> findAllByUser(UserEntity user);
}
