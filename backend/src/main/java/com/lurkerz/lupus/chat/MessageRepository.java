package com.lurkerz.lupus.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findByLobbyIdOrderBySentAtAsc(UUID lobbyId);
    List<MessageEntity> findByGameIdOrderBySentAtAsc(UUID gameId);
}
