package com.auth.service;

import com.auth.dto.role.UserRoleMapper;
import com.auth.dto.role.UserRoleResponse;
import com.auth.entity.UserEntity;
import com.auth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserEntityRepository userEntityRepository;
    private final UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userAuth = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        var userRoles = userRoleMapper.toResponse(userAuth); // получаем все данные по ролям

        // выводим только имя ролей
        var authorities = userRoles.rolesResponse().stream()
                .map(UserRoleResponse::roleName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return User
                .withUsername(userAuth.getUsername())
                .password(userAuth.getPassword())
                .authorities(authorities)
                .build();
    }
}
