package org.example.users.repository;

import org.example.users.repository.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {
    UserEntity save(UserEntity user);
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    void delete(UserEntity user);
}
