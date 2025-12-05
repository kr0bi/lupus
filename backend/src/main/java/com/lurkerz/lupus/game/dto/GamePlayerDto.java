package com.lurkerz.lupus.game.dto;

import com.lurkerz.lupus.common.PlayerRole;

import java.util.UUID;

public record GamePlayerDto(
        UUID id,
        UUID userId,
        String username,
        PlayerRole role,
        boolean alive,
        int joinOrder
) {
}
