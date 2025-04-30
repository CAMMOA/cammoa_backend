package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.users.repository.entity.UserEntity;

import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoomEntity save(ChatRoomEntity chatRoom);
    Optional<ChatRoomEntity> findById(Long roomId);
    Optional<ChatRoomEntity> findByUser(UserEntity user);
}
