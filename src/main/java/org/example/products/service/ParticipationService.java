package org.example.products.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.ResourceException;
import org.example.products.dto.response.ProductSimpleResponse;
import org.example.products.repository.ParticipationRepository;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.entity.ParticipationEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.example.users.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ProductRepository productRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public boolean checkParticipationStatus(Long postId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.USER_NOT_FOUND));

        ProductEntity product = productRepository.findById(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));

        return participationRepository.existsByUserAndProduct(user, product);
    }

    @Transactional
    public void cancelParticipation(Long postId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.USER_NOT_FOUND));

        ProductEntity product = productRepository.findById(postId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.POST_NOT_FOUND));

        ParticipationEntity participation = participationRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.NOT_JOINED));

        // 삭제
        participationRepository.delete(participation);

        // 인원 수 감소
        product.setCurrentParticipants(product.getCurrentParticipants() - 1);
    }

    public List<ProductSimpleResponse> getParticipatedGroupBuyings(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorResponseEnum.USER_NOT_FOUND));

        List<ParticipationEntity> participations = participationRepository.findAllByUser(user);

        return participations.stream()
                .map(participation -> {
                    ProductEntity product = participation.getProduct();
                    return ProductSimpleResponse.builder()
                            .productId(product.getProductId())
                            .title(product.getTitle())
                            .imageUrl(product.getImage())
                            .currentParticipants(product.getCurrentParticipants())
                            .maxParticipants(product.getMaxParticipants())
                            .build();
                })
                .collect(Collectors.toList());
    }



}
