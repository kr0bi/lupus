package com.lurkerz.lupus.game.dto;

import com.lurkerz.lupus.common.GamePhase;
import com.lurkerz.lupus.common.GameStatus;

import java.time.Instant;
import java.util.UUID;

public record GameSummaryDto(
        UUID id,
        UUID lobbyId,
        GameStatus status,
        GamePhase currentPhase,
        int playerCount,
        Instant createdAt
) {
}
