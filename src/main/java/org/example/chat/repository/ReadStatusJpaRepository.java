package org.example.chat.repository;

import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.chat.repository.entity.ReadStatusEntity;
import org.example.users.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusJpaRepository extends JpaRepository<ReadStatusEntity, Long> {
    ReadStatusEntity save(ReadStatusEntity readStatus);

    // 사용자가 읽지 않은 메시지 개수
    Long countByChatRoomAndUserAndIsReadFalse(ChatRoomEntity chatRoom, UserEntity user);
    // 메시지를 아직 읽지 않은 전체 인원 수
    Long countByChatMessageAndIsReadFalse(ChatMessageEntity chatMessage);
}
