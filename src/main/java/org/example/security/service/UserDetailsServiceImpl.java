package org.example.security.service;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    //User 데이터가 존재한다면 UserDetails 객체로 만듦
    public UserDetails createUserDetails(UserEntity user){
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
