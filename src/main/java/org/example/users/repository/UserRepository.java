package org.example.users.repository;

import org.example.users.repository.entity.UserEntity;

public interface UserRepository {
    UserEntity save(UserEntity user);
}
