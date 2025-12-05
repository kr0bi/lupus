package com.lurkerz.lupus.game;

import com.lurkerz.lupus.game.dto.GameStateDto;
import com.lurkerz.lupus.game.dto.GameSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSummaryDto> getSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(gameService.getSummary(id));
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDto> getState(@PathVariable UUID id) {
        return ResponseEntity.ok(gameService.getState(id));
    }
}
