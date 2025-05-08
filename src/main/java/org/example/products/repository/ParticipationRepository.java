package org.example.products.repository;

import org.example.products.repository.entity.ParticipationEntity;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<ParticipationEntity, Long> {
    boolean existsByUserAndProduct(UserEntity user, ProductEntity product);
    Optional<ParticipationEntity> findByUserAndProduct(UserEntity user, ProductEntity product);
}
