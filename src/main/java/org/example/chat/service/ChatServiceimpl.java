package org.example.chat.service;

import lombok.AllArgsConstructor;
import org.example.chat.dto.request.ChatMessageRequest;
import org.example.chat.dto.response.CreateChatRoomResponse;
import org.example.chat.dto.response.GetChatRoomsResponse;
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
import org.example.exception.impl.ResourceException;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.common.ResponseEnum.ErrorResponseEnum.CHATROOM_NOT_FOUND;
import static org.example.common.ResponseEnum.ErrorResponseEnum.POST_NOT_FOUND;

@Service
@Transactional
@AllArgsConstructor
public class ChatServiceimpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

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

    public CreateChatRoomResponse createChatRoom(Long productId, String chatRoomName) {
        //이메일로 수정해야 함 (로그인 리팩토링할 때 수정)
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(principal);
        UserEntity user = userRepository.findByUsername(principal)
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        //상품 조회
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceException(POST_NOT_FOUND));

        //채팅방 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(chatRoomName)
                .product(product)
                .build();
        chatRoomRepository.save(chatRoom);

        //채팅참여자로 개설자 추가
        ChatParticipantEntity chatParticipant = ChatParticipantEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);

        return new CreateChatRoomResponse(chatRoom.getChatRoomId(), chatRoom.getChatRoomName());
    }

    public List<String> joinChatRoom(Long roomId) {
        //채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new ChatException(CHATROOM_NOT_FOUND));

        //유저 조회
        //이메일로 수정해야 함 (로그인 리팩토링할 때 수정)
        UserEntity user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        //참여자인지 확인
        Optional<ChatParticipantEntity> participant = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user);
        if(!participant.isPresent()){
            addParticipantToRoom(chatRoom, user);
        }

        //전체 참여자 이름 리스트 반환
        List<ChatParticipantEntity> participants = chatParticipantRepository.findByChatRoom(chatRoom);
        List<String> participantNicknames = participants.stream()
                .map(p -> p.getUser().getNickname())
                .collect(Collectors.toList());

        return participantNicknames;
    }

    //ChatParticipant객체 생성 후 저장
    public void addParticipantToRoom(ChatRoomEntity chatRoom, UserEntity user) {
        ChatParticipantEntity chatParticipant = ChatParticipantEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }
}
