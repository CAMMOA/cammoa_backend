package org.example.chat.service;

import lombok.AllArgsConstructor;
import org.example.chat.dto.request.ChatMessageRequest;
import org.example.chat.repository.ChatMessageRepository;
import org.example.chat.repository.ChatParticipantRepository;
import org.example.chat.repository.ChatRoomRepository;
import org.example.chat.repository.ReadStatusRepository;
import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.chat.repository.entity.ReadStatusEntity;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.example.exception.impl.ChatException;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ChatServiceimpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    public void saveMessage(Long roomId, ChatMessageRequest request) {
        //채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND));

        //보낸사람 조회
        UserEntity sender = userRepository.findByEmail(request.getSenderEmail())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        //메시지저장
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(request.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        //사용자별 읽음여부 저장
        List<ChatParticipantEntity> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipantEntity c : chatParticipants) {
            ReadStatusEntity readStatus = ReadStatusEntity.builder()
                    .chatRoom(chatRoom)
                    .user(c.getUser())
                    .chatMessage(chatMessage)
                    .isRead(c.getUser().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
    }
}
