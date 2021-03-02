package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.move.MoveValidator;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedValidator;
import com.destrostudios.grid.util.CalculationUtils;
import com.destrostudios.grid.util.GameOverUtils;
import com.destrostudios.turnbasedgametools.bot.BotActionReplay;
import com.destrostudios.turnbasedgametools.bot.BotGameState;
import java.util.ArrayList;
import java.util.Collections;
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
        EntityWorld world = game.getWorld();
        for (int entity : world.list(TurnComponent.class)) {
            String playerIdentifier = Integer.toString(entity);
            PositionComponent position = world.getComponent(entity, PositionComponent.class);
            MovementPointsComponent mp = world.getComponent(entity, MovementPointsComponent.class);
            if (position != null && mp != null && mp.getMovementPoints() > 0) {
                tryAddMoveAction(position.getX(), position.getY() + 1, playerIdentifier, world, entity, actions);
                tryAddMoveAction(position.getX(), position.getY() - 1, playerIdentifier, world, entity, actions);
                tryAddMoveAction(position.getX() + 1, position.getY(), playerIdentifier, world, entity, actions);
                tryAddMoveAction(position.getX() - 1, position.getY(), playerIdentifier, world, entity, actions);
            }
            SpellsComponent spells = world.getComponent(entity, SpellsComponent.class);
            if (spells != null) {
                for (int spell : spells.getSpells()) {
                    SpellCastedValidator validator = new SpellCastedValidator();
                    List<PositionComponent> targetable = CalculationUtils.getRangePosComponents(spell, entity, world);
                    for (PositionComponent target : targetable) {
                        if (validator.validate(new SpellCastedEvent(spell, entity, target.getX(), target.getY()), () -> world)) {
                            actions.add(new CastSpellAction(target.getX(), target.getY(), playerIdentifier, spell));
                        }
                    }
                }
            }

            actions.add(new SkipRoundAction(playerIdentifier));
        }
        Collections.shuffle(actions);
        return actions;
    }

    private static void tryAddMoveAction(int x, int y, String playerIdentifier, EntityWorld world, int entity, List<Action> actions) {
        MoveValidator validator = new MoveValidator();
        if (validator.validate(new MoveEvent(entity, new PositionComponent(x, y)), () -> world)) {
            actions.add(new PositionUpdateAction(x, y, playerIdentifier));
        }
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
