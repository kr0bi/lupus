package com.lurkerz.lupus.game;

import com.lurkerz.lupus.common.GamePhase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "votes")
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private GamePlayerEntity voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private GamePlayerEntity target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GamePhase phase;

    @CreationTimestamp
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }

    public GamePlayerEntity getVoter() {
        return voter;
    }

    public void setVoter(GamePlayerEntity voter) {
        this.voter = voter;
    }

    public GamePlayerEntity getTarget() {
        return target;
    }

    public void setTarget(GamePlayerEntity target) {
        this.target = target;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
