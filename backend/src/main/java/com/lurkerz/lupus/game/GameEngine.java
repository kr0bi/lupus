package com.lurkerz.lupus.game;

import com.lurkerz.lupus.common.GameStatus;
import com.lurkerz.lupus.common.PlayerRole;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GameEngine {

    public List<PlayerRole> assignRoles(int playerCount) {
        List<PlayerRole> roles = new ArrayList<>();
        int werewolves = Math.max(1, playerCount / 4);
        for (int i = 0; i < werewolves; i++) {
            roles.add(PlayerRole.WEREWOLF);
        }
        if (playerCount >= 5) {
            roles.add(PlayerRole.SEER);
        }
        if (playerCount >= 6) {
            roles.add(PlayerRole.DOCTOR);
        }
        while (roles.size() < playerCount) {
            roles.add(PlayerRole.VILLAGER);
        }
        Collections.shuffle(roles);
        return roles;
    }

    public Optional<GamePlayerEntity> resolveDayLynch(List<GamePlayerEntity> players, List<VoteEntity> votes) {
        Map<UUID, Long> voteCount = votes.stream()
                .filter(v -> v.getTarget() != null)
                .collect(Collectors.groupingBy(v -> v.getTarget().getId(), Collectors.counting()));
        if (voteCount.isEmpty()) {
            return Optional.empty();
        }
        long max = voteCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
        List<UUID> topTargets = voteCount.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();
        if (topTargets.size() != 1) {
            return Optional.empty(); // tie -> no lynch
        }
        UUID eliminatedId = topTargets.get(0);
        return players.stream().filter(p -> p.getId().equals(eliminatedId)).findFirst();
    }

    public GameStatus evaluateStatus(List<GamePlayerEntity> players) {
        long wolves = players.stream().filter(p -> p.isAlive() && p.getRole() == PlayerRole.WEREWOLF).count();
        long villagers = players.stream().filter(p -> p.isAlive() && p.getRole() != PlayerRole.WEREWOLF).count();
        if (wolves == 0) {
            return GameStatus.FINISHED;
        }
        if (wolves >= villagers) {
            return GameStatus.FINISHED;
        }
        return GameStatus.RUNNING;
    }
}
