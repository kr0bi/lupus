package com.lurkerz.lupus.game.dto;

import com.lurkerz.lupus.common.GamePhase;
import com.lurkerz.lupus.common.GameStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameStateDto(
        UUID id,
        GameStatus status,
        GamePhase phase,
        List<GamePlayerDto> players,
        Instant updatedAt
) {
}
