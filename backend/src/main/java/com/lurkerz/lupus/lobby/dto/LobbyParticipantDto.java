package com.lurkerz.lupus.lobby.dto;

import java.time.Instant;
import java.util.UUID;

public record LobbyParticipantDto(
        UUID userId,
        String username,
        Instant joinedAt
) {
}
