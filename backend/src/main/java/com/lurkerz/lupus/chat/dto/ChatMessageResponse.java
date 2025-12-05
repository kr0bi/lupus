package com.lurkerz.lupus.chat.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID lobbyId,
        UUID gameId,
        UUID senderId,
        String senderUsername,
        String content,
        Instant sentAt
) {
}
