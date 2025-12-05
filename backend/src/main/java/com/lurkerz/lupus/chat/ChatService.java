package com.lurkerz.lupus.chat;

import com.lurkerz.lupus.chat.dto.ChatMessageRequest;
import com.lurkerz.lupus.chat.dto.ChatMessageResponse;
import com.lurkerz.lupus.common.NotFoundException;
import com.lurkerz.lupus.game.GameEntity;
import com.lurkerz.lupus.game.GameRepository;
import com.lurkerz.lupus.lobby.LobbyEntity;
import com.lurkerz.lupus.lobby.LobbyRepository;
import com.lurkerz.lupus.user.UserEntity;
import com.lurkerz.lupus.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(MessageRepository messageRepository,
                       UserRepository userRepository,
                       LobbyRepository lobbyRepository,
                       GameRepository gameRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public ChatMessageResponse sendLobbyMessage(UUID lobbyId, UUID senderId, ChatMessageRequest request) {
        LobbyEntity lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new NotFoundException("Lobby not found"));
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        MessageEntity message = new MessageEntity();
        message.setLobby(lobby);
        message.setSender(sender);
        message.setContent(request.content());
        MessageEntity saved = messageRepository.save(message);
        ChatMessageResponse response = new ChatMessageResponse(saved.getId(), lobbyId, null, senderId, sender.getUsername(), saved.getContent(), saved.getSentAt());
        messagingTemplate.convertAndSend("/topic/lobby." + lobbyId + ".chat", response);
        return response;
    }

    @Transactional
    public ChatMessageResponse sendGameMessage(UUID gameId, UUID senderId, ChatMessageRequest request) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        MessageEntity message = new MessageEntity();
        message.setGame(game);
        message.setSender(sender);
        message.setContent(request.content());
        MessageEntity saved = messageRepository.save(message);
        ChatMessageResponse response = new ChatMessageResponse(saved.getId(), null, gameId, senderId, sender.getUsername(), saved.getContent(), saved.getSentAt());
        messagingTemplate.convertAndSend("/topic/game." + gameId + ".chat", response);
        return response;
    }
}
