package com.lurkerz.lupus.game.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VoteRequest(@NotNull UUID targetPlayerId) {
}
