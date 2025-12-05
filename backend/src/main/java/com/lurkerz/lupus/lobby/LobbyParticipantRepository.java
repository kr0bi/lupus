package com.lurkerz.lupus.lobby;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LobbyParticipantRepository extends JpaRepository<LobbyParticipantEntity, UUID> {
    List<LobbyParticipantEntity> findByLobbyId(UUID lobbyId);
    long countByLobbyId(UUID lobbyId);
    Optional<LobbyParticipantEntity> findByLobbyIdAndUserId(UUID lobbyId, UUID userId);
    boolean existsByLobbyIdAndUserId(UUID lobbyId, UUID userId);
    void deleteByLobbyIdAndUserId(UUID lobbyId, UUID userId);
}
