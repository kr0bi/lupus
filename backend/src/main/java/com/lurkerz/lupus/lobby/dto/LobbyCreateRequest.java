package com.lurkerz.lupus.lobby.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LobbyCreateRequest(
        @NotBlank @Size(min = 3, max = 100) String name,
        @Min(4) @Max(16) int maxPlayers
) {
}
