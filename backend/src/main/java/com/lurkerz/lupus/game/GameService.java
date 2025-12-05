package com.lurkerz.lupus.game;

import com.lurkerz.lupus.common.BadRequestException;
import com.lurkerz.lupus.common.GamePhase;
import com.lurkerz.lupus.common.GameStatus;
import com.lurkerz.lupus.common.NotFoundException;
import com.lurkerz.lupus.common.PlayerRole;
import com.lurkerz.lupus.game.dto.GamePlayerDto;
import com.lurkerz.lupus.game.dto.GameStateDto;
import com.lurkerz.lupus.game.dto.GameSummaryDto;
import com.lurkerz.lupus.lobby.LobbyEntity;
import com.lurkerz.lupus.lobby.LobbyParticipantRepository;
import com.lurkerz.lupus.user.UserEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository playerRepository;
    private final VoteRepository voteRepository;
    private final LobbyParticipantRepository lobbyParticipantRepository;
    private final GameEngine gameEngine;
    private final SimpMessagingTemplate messagingTemplate;

    public GameService(GameRepository gameRepository,
                       GamePlayerRepository playerRepository,
                       VoteRepository voteRepository,
                       LobbyParticipantRepository lobbyParticipantRepository,
                       GameEngine gameEngine,
                       SimpMessagingTemplate messagingTemplate) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.voteRepository = voteRepository;
        this.lobbyParticipantRepository = lobbyParticipantRepository;
        this.gameEngine = gameEngine;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public GameEntity startGameFromLobby(LobbyEntity lobby) {
        GameEntity game = new GameEntity();
        game.setLobby(lobby);
        game.setStatus(GameStatus.RUNNING);
        game.setCurrentPhase(GamePhase.DAY);
        GameEntity savedGame = gameRepository.save(game);
        var participants = lobbyParticipantRepository.findByLobbyId(lobby.getId());
        participants.sort(Comparator.comparing(p -> p.getJoinedAt()));
        List<PlayerRole> roles = gameEngine.assignRoles(participants.size());
        List<GamePlayerEntity> players = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            var participant = participants.get(i);
            GamePlayerEntity player = new GamePlayerEntity();
            player.setGame(savedGame);
            player.setUser(participant.getUser());
            player.setRole(roles.get(i));
            player.setAlive(true);
            player.setJoinOrder(i);
            players.add(player);
        }
        playerRepository.saveAll(players);
        broadcastState(toStateDto(savedGame));
        return savedGame;
    }

    @Transactional(readOnly = true)
    public GameSummaryDto getSummary(UUID gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));
        int playerCount = playerRepository.findByGameId(gameId).size();
        return new GameSummaryDto(game.getId(), game.getLobby().getId(), game.getStatus(), game.getCurrentPhase(), playerCount, game.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public GameStateDto getState(UUID gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));
        return toStateDto(game);
    }

    @Transactional
    public GameStateDto registerVote(UUID gameId, UUID voterUserId, UUID targetPlayerId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));
        if (game.getCurrentPhase() != GamePhase.DAY) {
            throw new BadRequestException("Voting is only allowed during the day");
        }
        GamePlayerEntity voter = playerRepository.findByGameIdAndUserId(gameId, voterUserId)
                .orElseThrow(() -> new BadRequestException("Player not part of this game"));
        if (!voter.isAlive()) {
            throw new BadRequestException("Dead players cannot vote");
        }
        GamePlayerEntity target = playerRepository.findById(targetPlayerId)
                .orElseThrow(() -> new BadRequestException("Target not found"));
        // Remove previous vote by voter in this phase
        voteRepository.findByGameIdAndPhase(gameId, GamePhase.DAY).stream()
                .filter(v -> v.getVoter().getId().equals(voter.getId()))
                .forEach(voteRepository::delete);

        VoteEntity vote = new VoteEntity();
        vote.setGame(game);
        vote.setVoter(voter);
        vote.setTarget(target);
        vote.setPhase(GamePhase.DAY);
        voteRepository.save(vote);

        var allAlive = playerRepository.findByGameId(gameId).stream().filter(GamePlayerEntity::isAlive).count();
        var votes = voteRepository.findByGameIdAndPhase(gameId, GamePhase.DAY);
        if (votes.size() >= allAlive) {
            gameEngine.resolveDayLynch(playerRepository.findByGameId(gameId), votes)
                    .ifPresent(eliminated -> {
                        eliminated.setAlive(false);
                        playerRepository.save(eliminated);
                    });
            voteRepository.deleteAll(votes);
            game.setCurrentPhase(GamePhase.NIGHT);
        }
        game.setStatus(gameEngine.evaluateStatus(playerRepository.findByGameId(gameId)));
        GameEntity saved = gameRepository.save(game);
        GameStateDto state = toStateDto(saved);
        broadcastState(state);
        return state;
    }

    @Transactional
    public GameStateDto registerNightAction(UUID gameId, UUID actorUserId, UUID targetPlayerId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));
        if (game.getCurrentPhase() != GamePhase.NIGHT) {
            throw new BadRequestException("Night actions only allowed during night");
        }
        GamePlayerEntity actor = playerRepository.findByGameIdAndUserId(gameId, actorUserId)
                .orElseThrow(() -> new BadRequestException("Player not part of this game"));
        if (!actor.isAlive()) {
            throw new BadRequestException("Dead players cannot act");
        }
        GamePlayerEntity target = playerRepository.findById(targetPlayerId)
                .orElseThrow(() -> new BadRequestException("Target not found"));
        if (actor.getRole() == PlayerRole.WEREWOLF) {
            target.setAlive(false);
            playerRepository.save(target);
        }
        game.setStatus(gameEngine.evaluateStatus(playerRepository.findByGameId(gameId)));
        if (game.getStatus() == GameStatus.RUNNING) {
            game.setCurrentPhase(GamePhase.DAY);
        }
        GameEntity saved = gameRepository.save(game);
        GameStateDto state = toStateDto(saved);
        broadcastState(state);
        return state;
    }

    private GameStateDto toStateDto(GameEntity game) {
        List<GamePlayerDto> players = playerRepository.findByGameId(game.getId()).stream()
                .sorted(Comparator.comparingInt(GamePlayerEntity::getJoinOrder))
                .map(this::mapPlayer)
                .toList();
        return new GameStateDto(
                game.getId(),
                game.getStatus(),
                game.getCurrentPhase(),
                players,
                game.getUpdatedAt()
        );
    }

    private GamePlayerDto mapPlayer(GamePlayerEntity player) {
        UserEntity user = player.getUser();
        return new GamePlayerDto(
                player.getId(),
                user.getId(),
                user.getUsername(),
                player.getRole(),
                player.isAlive(),
                player.getJoinOrder()
        );
    }

    private void broadcastState(GameStateDto state) {
        messagingTemplate.convertAndSend("/topic/game." + state.id(), state);
    }
}
