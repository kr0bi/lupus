package com.lurkerz.lupus.game;

import com.lurkerz.lupus.common.CurrentUser;
import com.lurkerz.lupus.game.dto.GameStateDto;
import com.lurkerz.lupus.game.dto.NightActionRequest;
import com.lurkerz.lupus.game.dto.VoteRequest;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class GameWebSocketController {

    private final GameService gameService;

    public GameWebSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/game.{gameId}.vote")
    public GameStateDto vote(@DestinationVariable UUID gameId, @Valid @Payload VoteRequest voteRequest) {
        UUID userId = CurrentUser.id();
        return gameService.registerVote(gameId, userId, voteRequest.targetPlayerId());
    }

    @MessageMapping("/game.{gameId}.nightAction")
    public GameStateDto nightAction(@DestinationVariable UUID gameId, @Valid @Payload NightActionRequest request) {
        UUID userId = CurrentUser.id();
        return gameService.registerNightAction(gameId, userId, request.targetPlayerId());
    }
}
