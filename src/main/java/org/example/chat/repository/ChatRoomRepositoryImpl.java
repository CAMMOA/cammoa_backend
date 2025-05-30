package org.example.chat.repository;

import lombok.AllArgsConstructor;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Override
    public ChatRoomEntity save(ChatRoomEntity chatRoom){return chatRoomJpaRepository.save(chatRoom);}

    @Override
    public Optional<ChatRoomEntity> findById(Long roomId){return chatRoomJpaRepository.findById(roomId);}

    @Override
    public List<ChatRoomEntity> findByChatParticipantsUser(UserEntity user){return chatRoomJpaRepository.findByChatParticipantsUser(user);}

    @Override
    public Optional<ChatRoomEntity> findByProduct(ProductEntity product){return chatRoomJpaRepository.findByProduct(product);}
}
