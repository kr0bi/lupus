package com.lurkerz.lupus.game;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GamePlayerRepository extends JpaRepository<GamePlayerEntity, UUID> {
    List<GamePlayerEntity> findByGameId(UUID gameId);
    Optional<GamePlayerEntity> findByGameIdAndUserId(UUID gameId, UUID userId);
}
