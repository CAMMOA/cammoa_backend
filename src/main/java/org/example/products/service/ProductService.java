package org.example.products.service;

import lombok.RequiredArgsConstructor;
import org.example.chat.dto.response.JoinChatRoomResponse;
import org.example.chat.repository.ChatParticipantRepository;
import org.example.chat.repository.ChatRoomRepository;
import org.example.chat.repository.entity.ChatParticipantEntity;
import org.example.chat.repository.entity.ChatRoomEntity;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.email.service.EmailService;
import org.example.exception.CustomException;
import org.example.exception.impl.AuthException;
import org.example.exception.impl.ChatException;
import org.example.exception.impl.ResourceException;
import org.example.products.constant.SortTypeEnum;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.request.ProductUpdateRequest;
import org.example.products.dto.response.*;
import org.example.products.repository.ParticipationRepository;
import org.example.products.repository.ProductImageRepository;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.entity.CategoryEnum;
import org.example.products.repository.entity.ParticipationEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.entity.ProductImageEntity;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final EmailService emailService;
    private final ProductImageRepository productImageRepository;

    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new ResourceException(ErrorResponseEnum.INVALID_DEADLINE);
        }

        if (request.getMaxParticipants() < request.getNumPeople()) {
            throw new ResourceException(ErrorResponseEnum.INVALID_MAX_PARTICIPANTS);

        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.USER_NOT_FOUND));


        ProductEntity product = ProductEntity.builder()
                .user(user)
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .image(request.getImage())
                .price(request.getPrice())
                .deadline(request.getDeadline())
                .numPeople(request.getNumPeople())
                .place(request.getPlace())
                .status(request.getStatus())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(1)  // 생성 시 본인 자동 참여
                .build();

        ProductEntity savedProduct = productRepository.save(product);

        //채팅방 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(request.getTitle() + " 채팅방")
                .product(product)
                .build();
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        //채팅참여자로 개설자 추가
        ChatParticipantEntity chatParticipant = ChatParticipantEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);

        ProductResponse response = toProductResponse(savedProduct);
        response.setChatRoomId(savedChatRoom.getChatRoomId());
        response.setChatRoomName(savedChatRoom.getChatRoomName());

        return response;
    }

    //게시글 목록 조회
    public List<ProductListResponse> getAllProductsByCategory(CategoryEnum category) {
        List<ProductEntity> products;

        if (category != null) {
            products = productRepository.findByCategoryAndDeletedAtIsNull(category);
        } else {
            products = productRepository.findByDeletedAtIsNull();
        }

        LocalDateTime now = LocalDateTime.now();

        return products.stream()
                .filter(p -> p.getDeadline() == null || p.getDeadline().isAfter(now))
                .map(product -> ProductListResponse.builder()
                        .id(product.getProductId())
                        .title(product.getTitle())
                        .price(product.getPrice())
                        .imageUrl(product.getImage())
                        .deadline(product.getDeadline())
                        .maxParticipants(product.getMaxParticipants())
                        .build())
                .collect(Collectors.toList());
    }

    // 오늘의 공동구매 추천 (랜덤 8개)
    public List<ProductListResponse> getRecommendedProducts() {
        LocalDateTime now = LocalDateTime.now();
        return productRepository.findRandomRecommendedProducts().stream()
                .filter(p -> p.getDeadline() == null || p.getDeadline().isAfter(now))
                .map(ProductListResponse::from)
                .collect(Collectors.toList());
    }

    // 곧 마감되는 공구 (마감일 빠른 순 4개)
    public List<ProductListResponse> getClosingSoonProducts() {
        Pageable limit4 = PageRequest.of(0, 4);
        LocalDateTime now = LocalDateTime.now();
        return productRepository.findClosingSoonProducts(limit4)
                .getContent()
                .stream()
                .filter(p -> p.getDeadline() == null || p.getDeadline().isAfter(now))
                .map(ProductListResponse::from)
                .collect(Collectors.toList());
    }

    // 방금 올라온 공구 (생성일 최신순 4개)
    public List<ProductListResponse> getRecentlyPostedProducts() {
        Pageable limit4 = PageRequest.of(0, 4);
        LocalDateTime now = LocalDateTime.now();
        return productRepository.findRecentProducts(limit4)
                .getContent()
                .stream()
                .filter(p -> p.getDeadline() == null || p.getDeadline().isAfter(now))
                .map(ProductListResponse::from)
                .collect(Collectors.toList());
    }


    private ProductResponse toProductResponse(ProductEntity product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .category(product.getCategory().name())
                .description(product.getDescription())
                .imageUrl(product.getImage())
                .price(product.getPrice())
                .deadline(product.getDeadline())
                .numPeople(product.getNumPeople())
                .place(product.getPlace())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .status(product.getStatus())
                .currentParticipants(product.getCurrentParticipants())
                .maxParticipants(product.getMaxParticipants())
                .build();
    }

    public ProductDetailResponse getProductDetail(Long productId) {
        ProductEntity product = productRepository.findByIdWithUserAndNotDeleted(productId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND)); // 예외처리

        //게시글의 모든 이미지 URL 조회
        List<String> imageUrls = productImageRepository.findAllByProduct(product).stream()
                .map(ProductImageEntity::getImageUrl)
                .toList();

        // 작성자 본인의 다른 게시글 중 현재 게시글 제외하고 6개 랜덤 추출
        List<ProductSimpleResponse> relatedPosts = productRepository.findByUser(product.getUser()).stream()
                .filter(p -> !p.getProductId().equals(product.getProductId()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.shuffle(list);
                    return list.stream().limit(6).collect(Collectors.toList());
                }))
                .stream()
                .map(p -> ProductSimpleResponse.builder()
                        .productId(p.getProductId())
                        .title(p.getTitle())
                        .imageUrl(p.getImage())
                        .currentParticipants(p.getCurrentParticipants())
                        .maxParticipants(p.getMaxParticipants())
                        .price(p.getPrice())
                        .deadline(p.getDeadline())
                        .build())
                .toList();

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .category(product.getCategory().name())
                .imageUrl(imageUrls)
                .price(product.getPrice())
                .deadline(product.getDeadline())
                .place(product.getPlace())
                .currentParticipants(product.getCurrentParticipants())
                .maxParticipants(product.getMaxParticipants())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .username(product.getUser().getUsername())  // 작성자 이름
                .relatedPosts(relatedPosts)
                .build();
    }


    @Transactional
    public ProductResponse updateProduct(Long postId, ProductUpdateRequest request, Long userId) {
        ProductEntity product = productRepository.findByIdWithUserAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));

        if (!product.getUser().getId().equals(userId)) {
            throw new AuthException(ErrorResponseEnum.UNAUTHORIZED_ACCESS);
        }

        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDeadline() != null) product.setDeadline(request.getDeadline());
        if (request.getPlace() != null) product.setPlace(request.getPlace());
        if (request.getImage() != null) product.setImage(request.getImage());

        return toProductResponse(product);
    }

    public List<ProductResponse> searchProductsByKeywordAndCategory(String keyword, CategoryEnum category, SortTypeEnum sortTypeEnum) {
        if (sortTypeEnum == null) {
            sortTypeEnum = SortTypeEnum.DEADLINE;
        }

        if ((keyword == null || keyword.isBlank()) && category == null) {
            throw new ResourceException(ErrorResponseEnum.INVALID_SEARCH_CONDITION);
        }

        List<ProductEntity> products;

        // keyword가 없는 경우
        if (keyword == null || keyword.isBlank()) {
            products = productRepository.findByCategoryAndNotDeletedAndNotExpired(category);

            LocalDateTime now = LocalDateTime.now();
            products = products.stream()
                    .filter(p -> p.getDeadline() == null || p.getDeadline().isAfter(now))
                    .collect(Collectors.toList());

            // 정렬
            if (sortTypeEnum == SortTypeEnum.DEADLINE) {
                products.sort((p1, p2) -> p1.getDeadline().compareTo(p2.getDeadline()));
            } else if (sortTypeEnum == SortTypeEnum.RECENT) {
                products.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
            } else if (sortTypeEnum == SortTypeEnum.RECOMMEND) {
                Collections.shuffle(products);
            }
        }
        // keyword가 있는 경우 (기존 쿼리 활용)
        else {
            products = productRepository.searchProductsByKeywordAndCategory(
                    keyword,
                    category,
                    sortTypeEnum.name()
            );
        }


        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long postId, Long userId) {
        ProductEntity product = productRepository.findByIdWithUserAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));

        if (!product.getUser().getId().equals(userId)) {
            throw new AuthException(ErrorResponseEnum.UNAUTHORIZED_ACCESS);
        }
        //본인 외 다른 참여자가 존재하는지 확인
        List<ParticipationEntity> participants = participationRepository.findAllByProduct(product);
        boolean hasOtherParticipants = participants.stream()
                .anyMatch(p -> !p.getUser().getId().equals(userId));

        if (hasOtherParticipants) {
            throw new CustomException(ErrorResponseEnum.POST_HAS_PARTICIPANTS);
        }
        product.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public ChatRoomInfoResponse joinGroupBuying(Long postId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.USER_NOT_FOUND));

        ProductEntity product = productRepository.findByProductIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));
        //작성자 본인 참여 방지
        if (product.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorResponseEnum.WRITER_CANNOT_JOIN);
        }

        if (participationRepository.existsByUserAndProduct(user, product)) {
            throw new CustomException(ErrorResponseEnum.ALREADY_JOINED);
        }

        if (product.getDeadline().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorResponseEnum.POST_CLOSED);
        }

        if (product.getCurrentParticipants() >= product.getMaxParticipants()) {
            throw new CustomException(ErrorResponseEnum.POST_FULL);
        }

        ParticipationEntity participation = ParticipationEntity.builder()
                .user(user)
                .product(product)
                .joinedAt(LocalDateTime.now())
                .build();

        participationRepository.save(participation);

        // 현재 인원 수 증가
        product.setCurrentParticipants(product.getCurrentParticipants() + 1);

        if (product.getCurrentParticipants() >= product.getMaxParticipants()) {
            List<UserEntity> participants = participationRepository.findAllByProduct(product)
                    .stream()
                    .map(ParticipationEntity::getUser)
                    .collect(Collectors.toList());

            emailService.sendCompletionNotification(product, participants);
        }
        //채팅방 정보 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findByProduct(product)
                .orElseThrow(() -> new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND));

        return ChatRoomInfoResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomName(chatRoom.getChatRoomName())
                .build();
    }

    public JoinChatRoomResponse joinChatRoom(Long postId) {
        //게시글로 채팅방 조회
        ProductEntity product = productRepository.findById(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));

        ChatRoomEntity chatRoom = chatRoomRepository.findByProduct(product)
                .orElseThrow(() -> new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND));

        //유저 조회
        //이메일로 수정해야 함 (로그인 리팩토링할 때 수정)
        UserEntity user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        //참여자인지 확인
        Optional<ChatParticipantEntity> participant = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user);
        if (participant.isPresent()) {
            throw new ChatException(ErrorResponseEnum.DUPLICATED_PARTICIPANT);
        }

        addParticipantToRoom(chatRoom, user);

        return JoinChatRoomResponse.builder()
                .roomId(chatRoom.getChatRoomId())
                .roomName(chatRoom.getChatRoomName())
                .build();
    }

    //ChatParticipant 객체 생성 후 저장
    public void addParticipantToRoom(ChatRoomEntity chatRoom, UserEntity user) {
        ChatParticipantEntity chatParticipant = ChatParticipantEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }
    //모집 인원 완료시 알림 전송
    public void notifyGroupBuyingCompletion(Long postId) {
        ProductEntity product = productRepository.findById(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));

        if (product.getCurrentParticipants() < product.getMaxParticipants()) {
            throw new CustomException(ErrorResponseEnum.POST_NOT_COMPLETED);
        }

        List<UserEntity> participants = participationRepository.findAllByProduct(product)
                .stream()
                .map(ParticipationEntity::getUser)
                .collect(Collectors.toList());

        emailService.sendCompletionNotification(product, participants);
    }


}

