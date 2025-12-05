package com.lurkerz.lupus.lobby;

import com.lurkerz.lupus.common.LobbyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LobbyRepository extends JpaRepository<LobbyEntity, UUID> {
    List<LobbyEntity> findByStatus(LobbyStatus status);
}
