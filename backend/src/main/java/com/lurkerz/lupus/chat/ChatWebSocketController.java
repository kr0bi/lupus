package com.lurkerz.lupus.chat;

import com.lurkerz.lupus.chat.dto.ChatMessageRequest;
import com.lurkerz.lupus.chat.dto.ChatMessageResponse;
import com.lurkerz.lupus.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/lobby.{lobbyId}.chatMessage")
    public ChatMessageResponse sendLobbyMessage(@DestinationVariable UUID lobbyId,
                                                @Valid @Payload ChatMessageRequest request) {
        UUID userId = CurrentUser.id();
        return chatService.sendLobbyMessage(lobbyId, userId, request);
    }

    @MessageMapping("/game.{gameId}.chatMessage")
    public ChatMessageResponse sendGameMessage(@DestinationVariable UUID gameId,
                                               @Valid @Payload ChatMessageRequest request) {
        UUID userId = CurrentUser.id();
        return chatService.sendGameMessage(gameId, userId, request);
    }
}
