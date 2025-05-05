package org.example.chat.repository;

import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.users.repository.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository {
    ChatParticipantEntity save(ChatParticipantEntity chatParticipant);
    List<ChatParticipantEntity> findByChatRoom(ChatRoomEntity chatRoom);
    Optional<ChatParticipantEntity> findByChatRoomAndUser(ChatRoomEntity chatRoom, UserEntity user);
}
