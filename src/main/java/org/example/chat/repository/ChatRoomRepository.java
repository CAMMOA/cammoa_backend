package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoomEntity save(ChatRoomEntity chatRoom);
    Optional<ChatRoomEntity> findById(Long roomId);
    List<ChatRoomEntity> findByChatParticipantsUser(UserEntity user);
    Optional<ChatRoomEntity> findByProduct(ProductEntity product);
}
