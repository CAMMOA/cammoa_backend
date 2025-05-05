package org.example.chat.repository;

import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.products.repository.entity.ProductEntity;

import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoomEntity save(ChatRoomEntity chatRoom);
    Optional<ChatRoomEntity> findById(Long roomId);
    Optional<ChatRoomEntity> findByProduct(ProductEntity product);
}
