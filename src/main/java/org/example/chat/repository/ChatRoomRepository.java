package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;

import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoomEntity save(ChatRoomEntity chatRoom);
    Optional<ChatRoomEntity> findById(Long roomId);
}
