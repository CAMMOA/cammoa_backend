package org.example.users.repository;

import lombok.AllArgsConstructor;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserEntity save(UserEntity user){
        return userJpaRepository.save(user);
    }


}
