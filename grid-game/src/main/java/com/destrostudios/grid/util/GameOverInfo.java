package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.action.gameover.GameOverEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GameOverInfo {

    private final EntityData entityData;
    private final Eventbus eventbus;
    private boolean gameIsOver;
    private int winningTeamEntity;

    public GameOverInfo(EntityData entityData, Eventbus eventbus) {
        this.entityData = entityData;
        this.gameIsOver = false;
        this.winningTeamEntity = -1;
        this.eventbus = eventbus;
    }

    public void checkGameOverStatus() {
        if (gameIsOver) {
            return;
        }
        List<Integer> playerEntities = entityData.list(PlayerComponent.class);
        Map<Integer, List<Integer>> playersByTeam = new LinkedHashMap<>();

        for (Integer player : playerEntities) {
            TeamComponent teamComp = entityData.getComponent(player, TeamComponent.class);
            playersByTeam.computeIfAbsent(teamComp.getTeam(), s -> new ArrayList<>()).add(player);
        }
        AtomicInteger losingTeam = new AtomicInteger(-1);
        for (Map.Entry<Integer, List<Integer>> playersByTeamEntry : playersByTeam.entrySet()) {
            List<Integer> players = playersByTeamEntry.getValue();
            boolean teamNoHealth = players.stream().allMatch(e -> entityData.getComponent(e, HealthPointsComponent.class).getHealth() <= 0);
            if (teamNoHealth) {
                losingTeam.set(playersByTeamEntry.getKey());
            }
        }
        int winningTeam = playersByTeam.keySet().stream()
                .filter(e -> losingTeam.get() != -1 && e != losingTeam.get())
                .findFirst()
                .orElse(-1);

        this.gameIsOver = winningTeam != -1;
        this.winningTeamEntity = winningTeam;

        if (gameIsOver) {
            eventbus.registerSubEvents(new GameOverEvent(winningTeam));
        }
    }
}
