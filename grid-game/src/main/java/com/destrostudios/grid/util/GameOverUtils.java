package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GameOverUtils {

    public static GameOverInfo getGameOverInfo(EntityWorld entityWorld) {
        List<Integer> playerEntities = entityWorld.list(PlayerComponent.class);
        Map<Integer, List<Integer>> playersByTeam = new LinkedHashMap<>();

        for (Integer player : playerEntities) {
            TeamComponent teamComp = entityWorld.getComponent(player, TeamComponent.class);
            playersByTeam.computeIfAbsent(teamComp.getTeam(), s -> new ArrayList<>()).add(player);
        }
        final AtomicInteger loosingTeam = new AtomicInteger(-1);
        for (Map.Entry<Integer, List<Integer>> playersByTeamEntry : playersByTeam.entrySet()) {
            List<Integer> players = playersByTeamEntry.getValue();
            boolean teamNoHealth = players.stream().allMatch(e -> entityWorld.getComponent(e, HealthPointsComponent.class).getHealth() <= 0);
            if (teamNoHealth) {
                loosingTeam.set(playersByTeamEntry.getKey());
            }
        }
        int winningTeam = playersByTeam.keySet().stream()
                .filter(e -> loosingTeam.get() != -1 && e != loosingTeam.get())
                .findFirst()
                .orElse(-1);

        return new GameOverInfo(winningTeam != -1, winningTeam);
    }

    @Getter
    @AllArgsConstructor
    public static class GameOverInfo {
        private final boolean gameIsOver;
        private final int winningTeamEntity;
    }

}
