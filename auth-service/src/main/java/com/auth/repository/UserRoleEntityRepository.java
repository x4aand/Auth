package com.auth.repository;

import com.auth.entity.UserEntity;
import com.auth.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleEntityRepository extends JpaRepository<UserRoleEntity, Long> {

    Optional<UserRoleEntity> findById (Long id);

    Optional<UserRoleEntity> findByRoleName (String  nameRole);

}