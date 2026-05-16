package com.auth.service;

import com.auth.entity.UserRoleEntity;
import com.auth.repository.UserRoleEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final ConcurrentHashMap<String, UserRoleEntity> roleCache = new ConcurrentHashMap<>();
    private final UserRoleEntityRepository userRoleEntityRepository;

    /**
     * Возвращает UserRoleEntity по имени роли, используя кэш.
     * Внимание!!! Если будут изменения по ролям нужно очистить кэш
     * - Если роль уже есть в кэше (roleCache) — возвращает её.
     * - Если роли нет в кэше — выполняет запрос к БД, сохраняет результат в кэш и возвращает его.
     * @param roleName имя роли
     * @return найденная роль
     * @throws IllegalStateException если роль не найдена в БД
     */

    public UserRoleEntity getRoleFromCache(String roleName) {
        return roleCache.computeIfAbsent(roleName, key ->
                userRoleEntityRepository.findByRoleName(key)
                        .orElseThrow(() -> new IllegalStateException("Роль не найдена: " + key))
        );
    }
}
