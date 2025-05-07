package org.example.products.service;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.ResourceException;
import org.example.products.repository.ParticipationRepository;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Service;

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

}
