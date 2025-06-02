package org.example.users.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.response.GetChatRoomsResponse;
import org.example.chat.repository.ChatMessageRepository;
import org.example.chat.repository.ChatParticipantRepository;
import org.example.chat.repository.ReadStatusRepository;
import org.example.chat.repository.entity.ChatMessageEntity;
import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.email.dto.request.ValidateEmailRequest;
import org.example.email.dto.response.SendEmailResponse;
import org.example.email.service.EmailService;
import org.example.exception.impl.AuthException;
import org.example.exception.impl.ResourceException;
import org.example.products.dto.response.ProductSimpleResponse;
import org.example.products.repository.ParticipationRepository;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.entity.ParticipationEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.redis.RedisService;
import org.example.security.JwtTokenProvider;
import org.example.security.constant.JwtTokenConstant;
import org.example.security.dto.JwtToken;
import org.example.users.dto.request.ChangePasswordRequest;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.LoginResponse;
import org.example.users.dto.response.ProfileResponse;
import org.example.users.dto.response.UserResponse;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

import static org.example.security.constant.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ParticipationRepository participationRepository;

    @Override
    @Transactional
    public UserResponse signup(UserCreateRequest request) {

        //닉네임 중복 확인
        if(userRepository.existsByNickname(request.getNickname())){
            throw new ResourceException(ErrorResponseEnum.DUPLICATED_NICKNAME);
        }

        //이메일 중복 확인
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceException(ErrorResponseEnum.DUPLICATED_EMAIL);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        try {
            UserEntity userEntity = UserEntity.builder()
                    .nickname(request.getNickname())
                    .username(request.getUsername())
                    .password(encodedPassword)
                    .email(request.getEmail())
                    .roles(List.of(ROLE_USER))
                    .build();

            UserEntity savedUser = userRepository.save(userEntity);
            return UserResponse.from(savedUser);

        } catch (Exception e) {
            throw new AuthException(ErrorResponseEnum.RESPONSE_NOT_VALID);
        }
    }

    public SendEmailResponse sendAuthcode(String email) {
        try{
            String authCode = emailService.sendSimpleMessage(email);

            //Redis에 인증 코드 저장
            redisService.setCode(email, authCode);

            return SendEmailResponse.builder()
                    .authCode(authCode)
                    .build();

        } catch (MessagingException e) {
            throw new AuthException(ErrorResponseEnum.EMAIL_SEND_FAILED);
        } catch (DataAccessException e) {
            throw new AuthException(ErrorResponseEnum.REDIS_STORE_FAILURE);
        }
    }

    public void validationAuthCode(ValidateEmailRequest request) {

        String email = request.getEmail();
        String inputCode = request.getAuthCode().trim();

        String savedCode = redisService.getCode(email);

        if (StringUtils.isEmpty(savedCode)) {
            throw new AuthException(ErrorResponseEnum.AUTH_CODE_NOT_FOUND);
        }

        if (!savedCode.equals(inputCode)) {
            throw new AuthException(ErrorResponseEnum.AUTH_CODE_MISMATCH);
        }

        redisService.deleteData(email);
        redisService.setVerified(email);
    }

    public boolean isEmailVerified(String email) {
        return redisService.isVerified(email);
    }

    @Override
    @Transactional
    public LoginResponse login(String email, String password){
        // 1. 이메일로 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException(ErrorResponseEnum.INVALID_PASSWORD);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication, user.getId());

        return new LoginResponse(user.getId(), user.getNickname(),user.getEmail(), jwtToken.getAccessToken(), jwtToken.getRefreshToken());
    }

    @Override
    @Transactional
    public void logout(String accessToken){
        if(!StringUtils.hasText(accessToken) || !accessToken.startsWith("Bearer")){
            throw new AuthException(ErrorResponseEnum.INVALID_TOKEN);
        }

        String token = accessToken.replace(JwtTokenConstant.BEARER_PREFIX, "");

        long expirationMillis = jwtTokenProvider.getExpiration(token);

        if (expirationMillis <= 0) {
            throw new AuthException(ErrorResponseEnum.INVALID_TOKEN);
        }

        redisService.setBlackList(token, "logout", expirationMillis);
    }

    @Transactional
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        // 1. 이메일로 사용자 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        // 2. 기존 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthException(ErrorResponseEnum.INVALID_PASSWORD);
        }

        // 3. 새 비밀번호 암호화 및 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, String password){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        //비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException(ErrorResponseEnum.INVALID_PASSWORD);
        }

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public ProfileResponse getProfile(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));
        //내가 작성한 공구
        List<ProductEntity> products = productRepository.findByUserAndDeletedAtIsNull(user);

        List<ProductSimpleResponse> myGroupBuyings = products.stream()
                .map(product -> ProductSimpleResponse.builder()
                        .productId(product.getProductId())
                        .imageUrl(product.getImage())
                        .title(product.getTitle())
                        .currentParticipants(product.getCurrentParticipants())
                        .maxParticipants(product.getMaxParticipants())
                        .price(product.getPrice())
                        .deadline(product.getDeadline())
                        .build())
                .toList();
        //내가 참여한 공구
        List<ProductSimpleResponse> joinedGroupBuyings = participationRepository.findAllByUser(user).stream()
                .map(ParticipationEntity::getProduct)
                .filter(product -> product.getDeletedAt() == null && !product.getUser().getId().equals(userId)) // 삭제 안 됨 + 본인 글 제외
                .map(product -> ProductSimpleResponse.builder()
                        .productId(product.getProductId())
                        .imageUrl(product.getImage())
                        .title(product.getTitle())
                        .currentParticipants(product.getCurrentParticipants())
                        .maxParticipants(product.getMaxParticipants())
                        .price(product.getPrice())
                        .deadline(product.getDeadline())
                        .build())
                .toList();

        return ProfileResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .myGroupBuyings(myGroupBuyings)      // 작성한 공동구매 목록
                .joinedGroupBuyings(joinedGroupBuyings)  // 참여한 공동구매 목록 (지금은 빈 리스트)
                .notifications(new ArrayList<>())       // 나의 알림 목록 (지금은 빈 리스트)
                .build();
    }

    @Override
    @Transactional
    public List<ProductSimpleResponse> getMyGroupBuyings(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        List<ProductEntity> products = productRepository.findByUserAndDeletedAtIsNull(user);

        return products.stream()
                .map(product -> ProductSimpleResponse.builder()
                        .productId(product.getProductId())
                        .imageUrl(product.getImage())
                        .title(product.getTitle())
                        .currentParticipants(product.getCurrentParticipants())
                        .maxParticipants(product.getMaxParticipants())
                        .price(product.getPrice())
                        .deadline(product.getDeadline())
                        .build())
                .toList();
    }

    //채팅 목록 조회
    public List<GetChatRoomsResponse> getChatRooms(){
        UserEntity user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        List<ChatParticipantEntity> chatParticipants = chatParticipantRepository.findAllByUser(user);
        List<GetChatRoomsResponse> getChatRooms = new ArrayList<>();

        for(ChatParticipantEntity c: chatParticipants) {
            ChatRoomEntity chatroom = c.getChatRoom();
            ProductEntity product = chatroom.getProduct();

            //마지막 메시지 조회
            ChatMessageEntity lastMessage = chatMessageRepository.findTopByChatRoomOrderByCreatedTimeDesc(chatroom);
            //읽지 않은 메시지 개수 조회
            Long count = readStatusRepository.countByChatRoomAndUserAndIsReadFalse(c.getChatRoom(), user);

            GetChatRoomsResponse.LastMessageDto lastMessageDto = null;
            if (lastMessage != null) {
                lastMessageDto = GetChatRoomsResponse.LastMessageDto.builder()
                        .content(lastMessage.getContent())
                        .time(lastMessage.getCreatedTime())
                        .build();
            }

            ProductSimpleResponse productResponse = ProductSimpleResponse.builder()
                    .productId(product.getProductId())
                    .imageUrl(product.getImage())
                    .title(product.getTitle())
                    .currentParticipants(product.getCurrentParticipants())
                    .maxParticipants(product.getMaxParticipants())
                    .price(product.getPrice())
                    .deadline(product.getDeadline())
                    .build();

            GetChatRoomsResponse getChatRoom = GetChatRoomsResponse.builder()
                    .roomId(c.getChatRoom().getChatRoomId())
                    .roomName(c.getChatRoom().getChatRoomName())
                    .unreadMessageCount(count)
                    .product(productResponse)
                    .lastMessage(lastMessageDto)
                    .build();
            getChatRooms.add(getChatRoom);
        }
        return getChatRooms;
    }
}
