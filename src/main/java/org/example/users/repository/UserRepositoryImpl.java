package org.example.users.repository;

import lombok.AllArgsConstructor;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserEntity save(UserEntity user){
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public  boolean existsByNickname(String nickname){
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {return userJpaRepository.findByEmail(email);}

    @Override
    public void delete(UserEntity user) {
        userJpaRepository.delete(user);
    }
}
