package com.lurkerz.lupus.game;

import com.lurkerz.lupus.common.GameStatus;
import com.lurkerz.lupus.common.PlayerRole;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private final GameEngine engine = new GameEngine();

    @Test
    void assignRolesAtLeastOneWerewolf() {
        List<PlayerRole> roles = engine.assignRoles(6);
        assertEquals(6, roles.size());
        assertTrue(roles.contains(PlayerRole.WEREWOLF));
        assertTrue(roles.stream().filter(r -> r == PlayerRole.WEREWOLF).count() >= 1);
    }

    @Test
    void resolveDayLynchChoosesHighestVote() {
        GamePlayerEntity target = player(PlayerRole.VILLAGER);
        GamePlayerEntity other = player(PlayerRole.WEREWOLF);
        VoteEntity vote1 = vote(target);
        VoteEntity vote2 = vote(target);
        VoteEntity vote3 = vote(other);
        var eliminated = engine.resolveDayLynch(List.of(target, other), List.of(vote1, vote2, vote3));
        assertTrue(eliminated.isPresent());
        assertEquals(target.getId(), eliminated.get().getId());
    }

    @Test
    void evaluateStatusEndsWhenWolvesGone() {
        GamePlayerEntity villager = player(PlayerRole.VILLAGER);
        GamePlayerEntity wolf = player(PlayerRole.WEREWOLF);
        wolf.setAlive(false);
        GameStatus status = engine.evaluateStatus(List.of(villager, wolf));
        assertEquals(GameStatus.FINISHED, status);
    }

    private GamePlayerEntity player(PlayerRole role) {
        GamePlayerEntity player = new GamePlayerEntity();
        player.setRole(role);
        player.setAlive(true);
        player.setJoinOrder(0);
        player.setUser(null);
        try {
            var field = GamePlayerEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(player, UUID.randomUUID());
        } catch (Exception ignored) {
        }
        return player;
    }

    private VoteEntity vote(GamePlayerEntity target) {
        VoteEntity vote = new VoteEntity();
        vote.setTarget(target);
        return vote;
    }
}
