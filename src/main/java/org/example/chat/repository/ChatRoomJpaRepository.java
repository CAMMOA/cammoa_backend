package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.products.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, Long> {
    ChatRoomEntity save(ChatRoomEntity chatRoom);
    Optional<ChatRoomEntity> findById(Long roomId);
    Optional<ChatRoomEntity> findByProduct(ProductEntity product);
}
