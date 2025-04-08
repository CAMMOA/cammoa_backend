package org.example.users.repository;

import org.example.users.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    UserEntity save(UserEntity user);
}
