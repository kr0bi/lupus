package com.lurkerz.lupus.lobby;

import com.lurkerz.lupus.common.BadRequestException;
import com.lurkerz.lupus.common.LobbyStatus;
import com.lurkerz.lupus.common.NotFoundException;
import com.lurkerz.lupus.game.GameService;
import com.lurkerz.lupus.lobby.dto.LobbyCreateRequest;
import com.lurkerz.lupus.lobby.dto.LobbyParticipantDto;
import com.lurkerz.lupus.lobby.dto.LobbyResponse;
import com.lurkerz.lupus.user.UserEntity;
import com.lurkerz.lupus.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final LobbyParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyService(LobbyRepository lobbyRepository,
                        LobbyParticipantRepository participantRepository,
                        UserRepository userRepository,
                        GameService gameService,
                        SimpMessagingTemplate messagingTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public List<LobbyResponse> listOpenLobbies() {
        return lobbyRepository.findByStatus(LobbyStatus.OPEN)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LobbyResponse createLobby(UUID hostUserId, LobbyCreateRequest request) {
        UserEntity host = userRepository.findById(hostUserId)
                .orElseThrow(() -> new NotFoundException("Host user not found"));
        LobbyEntity lobby = new LobbyEntity();
        lobby.setName(request.name());
        lobby.setMaxPlayers(request.maxPlayers());
        lobby.setHostUser(host);
        LobbyEntity saved = lobbyRepository.save(lobby);
        LobbyParticipantEntity hostParticipant = new LobbyParticipantEntity();
        hostParticipant.setLobby(saved);
        hostParticipant.setUser(host);
        participantRepository.save(hostParticipant);
        return toResponse(saved);
    }

    @Transactional
    public LobbyResponse joinLobby(UUID lobbyId, UUID userId) {
        LobbyEntity lobby = getLobbyOrThrow(lobbyId);
        if (lobby.getStatus() != LobbyStatus.OPEN) {
            throw new BadRequestException("Lobby is not open");
        }
        long count = participantRepository.countByLobbyId(lobbyId);
        if (count >= lobby.getMaxPlayers()) {
            throw new BadRequestException("Lobby is full");
        }
        if (participantRepository.existsByLobbyIdAndUserId(lobbyId, userId)) {
            return toResponse(lobby);
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        LobbyParticipantEntity participant = new LobbyParticipantEntity();
        participant.setLobby(lobby);
        participant.setUser(user);
        participantRepository.save(participant);
        LobbyResponse response = toResponse(lobby);
        broadcastLobbyUpdate(response);
        return response;
    }

    @Transactional
    public LobbyResponse leaveLobby(UUID lobbyId, UUID userId) {
        LobbyEntity lobby = getLobbyOrThrow(lobbyId);
        if (lobby.getHostUser().getId().equals(userId)) {
            throw new BadRequestException("Host cannot leave their lobby");
        }
        participantRepository.findByLobbyIdAndUserId(lobbyId, userId)
                .ifPresent(participantRepository::delete);
        LobbyResponse response = toResponse(lobby);
        broadcastLobbyUpdate(response);
        return response;
    }

    @Transactional
    public LobbyResponse startGame(UUID lobbyId, UUID hostUserId) {
        LobbyEntity lobby = getLobbyOrThrow(lobbyId);
        if (!lobby.getHostUser().getId().equals(hostUserId)) {
            throw new BadRequestException("Only the host can start the game");
        }
        long participants = participantRepository.countByLobbyId(lobbyId);
        if (participants < 4) {
            throw new BadRequestException("At least 4 players required to start");
        }
        lobby.setStatus(LobbyStatus.IN_PROGRESS);
        LobbyEntity saved = lobbyRepository.save(lobby);
        gameService.startGameFromLobby(saved);
        LobbyResponse response = toResponse(saved);
        broadcastLobbyUpdate(response);
        return response;
    }

    private LobbyEntity getLobbyOrThrow(UUID id) {
        return lobbyRepository.findById(id).orElseThrow(() -> new NotFoundException("Lobby not found"));
    }

    private LobbyResponse toResponse(LobbyEntity lobby) {
        List<LobbyParticipantDto> participantDtos = participantRepository.findByLobbyId(lobby.getId()).stream()
                .map(p -> new LobbyParticipantDto(p.getUser().getId(), p.getUser().getUsername(), p.getJoinedAt()))
                .toList();
        return new LobbyResponse(
                lobby.getId(),
                lobby.getName(),
                lobby.getStatus(),
                lobby.getMaxPlayers(),
                lobby.getHostUser().getId(),
                participantDtos.size(),
                participantDtos,
                lobby.getCreatedAt(),
                lobby.getUpdatedAt()
        );
    }

    private void broadcastLobbyUpdate(LobbyResponse lobbyResponse) {
        messagingTemplate.convertAndSend("/topic/lobby." + lobbyResponse.id(), lobbyResponse);
    }
}
