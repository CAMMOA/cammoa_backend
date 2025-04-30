package org.example.chat.repository;

import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;

import java.util.List;

public interface ChatParticipantRepository {
    ChatParticipantEntity save(ChatParticipantEntity chatParticipant);
    List<ChatParticipantEntity> findByChatRoom(ChatRoomEntity chatRoom);
}
