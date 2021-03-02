package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.util.GameOverUtils;
import com.destrostudios.turnbasedgametools.bot.BotActionReplay;
import com.destrostudios.turnbasedgametools.bot.BotGameState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridBotState implements BotGameState<Action, Team> {
    // TODO: Action parameter must implement equals

    final GridGame game;
    private final List<Team> teams;

    public GridBotState(GridGame game) {
        this.game = game;

        List<Integer> characters = game.getWorld().list(TeamComponent.class);
        Set<Team> teamsSet = new HashSet<>();
        for (int character : characters) {
            teamsSet.add(new Team(game.getWorld().getComponent(character, TeamComponent.class).getTeam()));
        }
        teams = new ArrayList<>(teamsSet);
        teams.sort(Comparator.comparingInt(Team::getTeam));
    }

    @Override
    public BotActionReplay<Action> applyAction(Action action) {
        BotActionReplay<Action> actionReplay = new BotActionReplay<>(action, new int[0]);
        game.registerAction(action);
        return actionReplay;
    }

    @Override
    public Team activeTeam() {
        List<Integer> characters = game.getWorld().list(TurnComponent.class);
        Set<Team> activeTeams = new HashSet<>();
        for (int character : characters) {
            activeTeams.add(new Team(game.getWorld().getComponent(character, TeamComponent.class).getTeam()));
        }
        if (activeTeams.size() != 1) {
            throw new IllegalStateException();
        }
        return activeTeams.iterator().next();
    }

    @Override
    public List<Action> generateActions(Team team) {
        List<Action> actions = new ArrayList<>();
        for (int entity : game.getWorld().list(TurnComponent.class)) {
            String playerIdentifier = Integer.toString(entity);
            PositionComponent position = game.getWorld().getComponent(entity, PositionComponent.class);
            MovementPointsComponent mp = game.getWorld().getComponent(entity, MovementPointsComponent.class);
            if (position != null && mp != null && mp.getMovementPoints() > 0) {
                actions.add(new PositionUpdateAction(position.getX(), position.getY() + 1, playerIdentifier));
                actions.add(new PositionUpdateAction(position.getX(), position.getY() - 1, playerIdentifier));
                actions.add(new PositionUpdateAction(position.getX() + 1, position.getY(), playerIdentifier));
                actions.add(new PositionUpdateAction(position.getX() - 1, position.getY(), playerIdentifier));
            }
            actions.add(new SkipRoundAction(playerIdentifier));
        }
        return actions;
    }

    @Override
    public List<BotActionReplay<Action>> getHistory() {
        return null;// implementation is optional & increases performance
    }

    @Override
    public boolean isGameOver() {
        return GameOverUtils.getGameOverInfo(game.getWorld()).isGameIsOver();
    }

    @Override
    public List<Team> getTeams() {
        return teams;
    }
}
