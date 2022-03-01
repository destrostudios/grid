package com.destrostudios.grid.bot;

import com.destrostudios.gametools.bot.BotActionReplay;
import com.destrostudios.gametools.bot.BotGameState;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedValidator;
import com.destrostudios.grid.eventbus.action.walk.WalkEvent;
import com.destrostudios.grid.eventbus.action.walk.WalkValidator;
import com.destrostudios.grid.util.SpellUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridBotState implements BotGameState<Action, Team> {

    final GridGame game;
    private final List<Team> teams;

    public GridBotState(GridGame game) {
        this.game = game;

        List<Integer> characters = game.getData().list(TeamComponent.class);
        Set<Team> teamsSet = new HashSet<>();
        for (int character : characters) {
            teamsSet.add(new Team(game.getData().getComponent(character, TeamComponent.class).getTeam()));
        }
        teams = new ArrayList<>(teamsSet);
        teams.sort(Comparator.comparingInt(Team::getTeam));
    }

    @Override
    public BotActionReplay<Action> applyAction(Action action) {
        game.registerAction(action);
        while (game.triggeredHandlersInQueue()) {
            game.triggerNextHandler();
        }
        return new BotActionReplay<>(action, new int[0]);// TODO: randomness
    }

    @Override
    public Team activeTeam() {
        List<Integer> characters = game.getData().list(ActiveTurnComponent.class);
        Set<Team> activeTeams = new HashSet<>();
        for (int character : characters) {
            activeTeams.add(new Team(game.getData().getComponent(character, TeamComponent.class).getTeam()));
        }
        if (activeTeams.size() != 1) {
            throw new IllegalStateException();
        }
        return activeTeams.iterator().next();
    }

    @Override
    public List<Action> generateActions(Team team) {
        List<Action> actions = new ArrayList<>();
        EntityData data = game.getData();
        for (int entity : data.list(ActiveTurnComponent.class)) {
            String playerIdentifier = Integer.toString(entity);
            PositionComponent position = data.getComponent(entity, PositionComponent.class);
            MovementPointsComponent mp = data.getComponent(entity, MovementPointsComponent.class);
            if (position != null && mp != null && mp.getMovementPoints() > 0) {
                tryAddMoveAction(position.getX(), position.getY() + 1, playerIdentifier, data, entity, actions);
                tryAddMoveAction(position.getX(), position.getY() - 1, playerIdentifier, data, entity, actions);
                tryAddMoveAction(position.getX() + 1, position.getY(), playerIdentifier, data, entity, actions);
                tryAddMoveAction(position.getX() - 1, position.getY(), playerIdentifier, data, entity, actions);
            }
            SpellCastedValidator validator = new SpellCastedValidator();
            SpellsComponent spells = data.getComponent(entity, SpellsComponent.class);
            if (spells != null) {
                for (int spell : spells.getSpells()) {
                    if (SpellUtils.isCastable(entity, spell, data)) {

//                        List<Integer> targets = RangeUtils.getAllTargetableEntitiesInRange(spell, entity, data);
//                        for (int target : targets) {
//                            PositionComponent targetPos = data.getComponent(target, PositionComponent.class);
//                            if (validator.validate(new SpellCastedEvent(spell, entity, targetPos.getX(), targetPos.getY()), () -> data)) {
//                                actions.add(new CastSpellAction(targetPos.getX(), targetPos.getY(), playerIdentifier, spell));
//                            }
//                        }

                        // simplified targeting, good enough for this early version
                        // Note: this prevents the AI from using skills like Jump
                        List<Integer> healthEntities = data.list(HealthPointsComponent.class);
                        for (int healthEntity : healthEntities) {
                            PositionComponent target = data.getComponent(healthEntity, PositionComponent.class);
                            if (target != null) {
                                if (validator.validate(new SpellCastedEvent(spell, entity, target.getX(), target.getY()), () -> data)) {
                                    actions.add(new CastSpellAction(target.getX(), target.getY(), playerIdentifier, spell));
                                }
                            }
                        }
                    }
                }
            }

            actions.add(new SkipRoundAction(playerIdentifier));
        }
        Collections.shuffle(actions);
        return actions;
    }

    private static void tryAddMoveAction(int x, int y, String playerIdentifier, EntityData data, int entity, List<Action> actions) {
        WalkValidator validator = new WalkValidator();
        if (validator.validate(new WalkEvent(entity, new PositionComponent(x, y)), () -> data)) {
            actions.add(new PositionUpdateAction(x, y, playerIdentifier));
        }
    }

    @Override
    public boolean isGameOver() {
        return game.getGameOverInfo().isGameIsOver();
    }

    @Override
    public List<Team> getTeams() {
        return teams;
    }
}
