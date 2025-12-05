package com.lurkerz.lupus.game;

import com.lurkerz.lupus.common.GamePhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<VoteEntity, UUID> {
    List<VoteEntity> findByGameIdAndPhase(UUID gameId, GamePhase phase);
}
