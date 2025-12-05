package com.lurkerz.lupus.lobby;

import com.lurkerz.lupus.common.CurrentUser;
import com.lurkerz.lupus.lobby.dto.LobbyCreateRequest;
import com.lurkerz.lupus.lobby.dto.LobbyResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping
    public ResponseEntity<List<LobbyResponse>> listOpen() {
        return ResponseEntity.ok(lobbyService.listOpenLobbies());
    }

    @PostMapping
    public ResponseEntity<LobbyResponse> create(@Valid @RequestBody LobbyCreateRequest request) {
        UUID userId = CurrentUser.id();
        return ResponseEntity.ok(lobbyService.createLobby(userId, request));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<LobbyResponse> join(@PathVariable UUID id) {
        UUID userId = CurrentUser.id();
        return ResponseEntity.ok(lobbyService.joinLobby(id, userId));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<LobbyResponse> leave(@PathVariable UUID id) {
        UUID userId = CurrentUser.id();
        return ResponseEntity.ok(lobbyService.leaveLobby(id, userId));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<LobbyResponse> start(@PathVariable UUID id) {
        UUID userId = CurrentUser.id();
        return ResponseEntity.ok(lobbyService.startGame(id, userId));
    }
}
