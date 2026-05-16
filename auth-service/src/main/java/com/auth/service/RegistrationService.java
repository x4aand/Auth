package com.auth.service;

import com.auth.component.RegistrationMainUserService;
import com.auth.dto.register.UserRegisterEntityMapper;
import com.auth.dto.register.UserRegisterRequest;
import com.auth.dto.register.UserRegisterResponse;
import com.auth.entity.UserEntity;
import com.auth.entity.UserRoleEntity;
import com.auth.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserEntityRepository userRepository;
    private final UserRegisterEntityMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final RegistrationMainUserService registrationMainUserService;

    @Transactional
    public UserRegisterResponse registration(UserRegisterRequest userRegisterRequest) {
        // проверяем логин
        if (userRepository.existsByUsername(userRegisterRequest.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // проверяем емайл
        if (userRepository.existsByEmail(userRegisterRequest.email())) {
            throw new IllegalArgumentException("Email already exists");
     }

        UserEntity userEntity = mapper.toEntity(userRegisterRequest); // копируем username, password, email создается Map структура UserEntity

        userEntity.setPassword(passwordEncoder.encode(userRegisterRequest.password())); // кодируем пароль

        UserRoleEntity userRoleEntity = roleService.getRoleFromCache("ROLE_USER");  // Инцелизируем  роль USER

        userEntity.setRoles(Set.of(userRoleEntity)); // сохраняем USER роль

        userRepository.save(userEntity); // сохраняем в БД
        // передает в сервис
        //  registrationMainUserService.createUser(userEntity.getUuid().toString(), userEntity.getUsername(), userEntity.getEmail());

        return mapper.toResponse(userEntity); // передаем UUID, username, email
    }
}
