package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.chat.repository.entity.ReadStatusEntity;
import org.example.users.repository.entity.UserEntity;

public interface ReadStatusRepository {
    ReadStatusEntity save(ReadStatusEntity readStatus);
    Long countByChatRoomAndUserAndIsReadFalse(ChatRoomEntity chatRoom, UserEntity user);
}
