package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.chat.repository.entity.ReadStatusEntity;
import org.example.users.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusJpaRepository extends JpaRepository<ReadStatusEntity, Long> {
    ReadStatusEntity save(ReadStatusEntity readStatus);

    Long countByChatRoomAndUserAndIsReadFalse(ChatRoomEntity chatRoom, UserEntity user);
}
