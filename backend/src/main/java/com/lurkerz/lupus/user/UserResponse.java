package com.lurkerz.lupus.user;

import com.lurkerz.lupus.common.Role;

import java.util.Set;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email, Set<Role> roles) {
    public static UserResponse from(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getRoles());
    }
}
