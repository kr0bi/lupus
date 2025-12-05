package com.lurkerz.lupus.lobby.dto;

import com.lurkerz.lupus.common.LobbyStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LobbyResponse(
        UUID id,
        String name,
        LobbyStatus status,
        int maxPlayers,
        UUID hostUserId,
        int participantCount,
        List<LobbyParticipantDto> participants,
        Instant createdAt,
        Instant updatedAt
) {
}
