package com.lurkerz.lupus.auth.dto;

import java.util.UUID;

public record AuthResponse(UserInfo user, Tokens tokens) {

    public record UserInfo(UUID id, String email, String username) {}

    public record Tokens(String accessToken, String refreshToken) {}

    public static AuthResponse of(UUID userId, String email, String username, String accessToken, String refreshToken) {
        return new AuthResponse(
            new UserInfo(userId, email, username),
            new Tokens(accessToken, refreshToken)
        );
    }
}
