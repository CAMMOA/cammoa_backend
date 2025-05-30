package org.example.chat.service;

import lombok.AllArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.repository.ChatMessageRepository;
import org.example.chat.repository.ChatParticipantRepository;
import org.example.chat.repository.ChatRoomRepository;
import org.example.chat.repository.ReadStatusRepository;
import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.chat.repository.entity.ReadStatusEntity;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.email.service.EmailService;
import org.example.exception.impl.AuthException;
import org.example.exception.impl.ChatException;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public void saveMessage(Long roomId, ChatMessageDto request) {
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
        for (ChatParticipantEntity c : chatParticipants) {
            ReadStatusEntity readStatus = ReadStatusEntity.builder()
                    .chatRoom(chatRoom)
                    .user(c.getUser())
                    .chatMessage(chatMessage)
                    .isRead(c.getUser().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }

        List<UserEntity> participants = chatParticipants.stream()
                .map(ChatParticipantEntity::getUser)
                .filter(user -> !user.equals(sender))
                .collect(Collectors.toList());

        emailService.sendChatNotification(participants, request, chatRoom);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId) {
        // 해당 채팅방의 참여자인지 확인
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND));

        UserEntity user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        List<ChatParticipantEntity> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        boolean check = false;
        for (ChatParticipantEntity c : chatParticipants) {
            if(c.getUser().equals(user)) {
                check = true;
            }
        }
        if(!check) throw new ChatException(ErrorResponseEnum.PARTICIPANT_NOT_FOUND);

        //특정 room에 대한 message 조회
        List<ChatMessageEntity> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for (ChatMessageEntity c : chatMessages) {
            Long unreadCount = readStatusRepository.countByChatMessageAndIsReadFalse(c);

            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .roomId(c.getChatRoom().getChatRoomId())
                    .message(c.getContent())
                    .senderNickname(c.getUser().getNickname())
                    .senderEmail(c.getUser().getEmail())
                    .unreadMessageCount(unreadCount)
                    .createdTime(c.getCreatedTime())
                    .build();
            chatMessageDtos.add(chatMessageDto);
        }
        return chatMessageDtos;
    }

    public boolean isRoomParticipant(String email, Long roomId){
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND));

        UserEntity user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        List<ChatParticipantEntity> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipantEntity c : chatParticipants) {
            if(c.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void leaveChatRoom(Long roomId) {
        //채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND));
        //현재 유저 조회
        UserEntity user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));
        //채팅 참여자 조회
        ChatParticipantEntity participant = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow(()-> new ChatException(ErrorResponseEnum.PARTICIPANT_NOT_FOUND));
        //게시글 조회
        ProductEntity product = chatRoom.getProduct();
        //게시글 생성자인지 확인
        boolean isCreator = product.getUser().getId().equals(user.getId());

        if (isCreator){
            //게시글이 삭제된 상태인지 확인
            if (product.getDeletedAt() == null){
                throw new ChatException(ErrorResponseEnum.CREATOR_CANNOT_LEAVE_CHATROOM);
            }
            chatParticipantRepository.delete(participant);
        } else{
            chatParticipantRepository.delete(participant);
        }
    }
}