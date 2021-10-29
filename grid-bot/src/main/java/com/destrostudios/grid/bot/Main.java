package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.ai.AiHintAllyManhattanDistanceScoresComponent;
import com.destrostudios.grid.components.ai.AiHintOpponentManhattanDistanceScoresComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverInfo;
import com.destrostudios.turnbasedgametools.bot.BotActionReplay;
import com.destrostudios.turnbasedgametools.bot.RolloutToEvaluation;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBot;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBotSettings;
import com.destrostudios.turnbasedgametools.grid.Heuristic;
import com.destrostudios.turnbasedgametools.grid.ManhattanHeuristic;
import com.destrostudios.turnbasedgametools.grid.Position;
import java.security.SecureRandom;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Heuristic MANHATTAN_HEURISTIC = new ManhattanHeuristic();

    public static void main(String... args) {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSSZ");
        Logger log = LoggerFactory.getLogger(com.destrostudios.grid.bot.Main.class);

        int strength = 1000;

        GridGame game = new GridGame();
        StartGameInfo gameInfo = StartGameInfo.getTestGameInfo();
        game.initGame(gameInfo);

        GridBotState botState = new GridBotState(game);
        MctsBot<GridBotState, Action, Team, SerializedGame> bot = createBot(strength);
        while (!game.getGameOverInfo().isGameIsOver()) {
            log.info("calculating...");
            long startNanos = System.nanoTime();
            List<Action> actions = bot.sortedActions(botState, botState.activeTeam());
            log.info("{}", actions);
            long durationNanos = System.nanoTime() - startNanos;
            log.info("Finished after {}.", humanReadableNanos(durationNanos));

            game.registerAction(actions.get(0));
            while (game.triggeredHandlersInQueue()) {
                game.triggerNextHandler();
            }
            bot.stepRoot(new BotActionReplay<>(actions.get(0), new int[0]));
        }
    }

    public static MctsBot<GridBotState, Action, Team, SerializedGame> createBot(int strength) {
        MctsBotSettings<GridBotState, Action> botSettings = new MctsBotSettings<>();
        botSettings.verbose = true;
        botSettings.maxThreads = 3;
        botSettings.strength = strength;
        botSettings.evaluation = new RolloutToEvaluation<>(new SecureRandom(), 5, Main::eval)::evaluate;

        return new MctsBot<>(new GridBotService(), botSettings);
    }

    private static float[] eval(GridBotState s) {
        float[] scores = new float[s.getTeams().size()];
        if (s.isGameOver()) {
            GameOverInfo gameOverInfo = s.game.getGameOverInfo();
            int winningTeam = gameOverInfo.getWinningTeamEntity();
            int teamIndex = s.getTeams().indexOf(new Team(winningTeam));
            scores[teamIndex] = 1;
        } else {
            float sum = 0;
            EntityData data = s.game.getData();
            for (int entity : data.list(TeamComponent.class)) {
                Team team = new Team(data.getComponent(entity, TeamComponent.class).getTeam());
                int teamIndex = s.getTeams().indexOf(team);
                HealthPointsComponent health = data.getComponent(entity, HealthPointsComponent.class);
                float value = 0;
                if (health != null) {
                    value += (float) health.getHealth() / data.getComponent(entity, MaxHealthComponent.class).getMaxHealth();
                }
//                if (data.hasComponents(entity, ActiveTurnComponent.class)) {
//                    AttackPointsComponent attackPoints = data.getComponent(entity, AttackPointsComponent.class);
//                    if (attackPoints != null) {
//                        value += 0.04 * attackPoints.getAttackPoints();
//                    }
//                    MovementPointsComponent movementPoints = data.getComponent(entity, MovementPointsComponent.class);
//                    if (movementPoints != null) {
//                        value += 0.02 * movementPoints.getMovementPoints();
//                    }
//                }
                PositionComponent positionComponent = data.getComponent(entity, PositionComponent.class);
                if (positionComponent != null) {
                    AiHintOpponentManhattanDistanceScoresComponent preferredOpponentDistance = data.getComponent(entity, AiHintOpponentManhattanDistanceScoresComponent.class);
                    if (preferredOpponentDistance != null) {
                        for (int other : data.list(TeamComponent.class)) {
                            if (entity == other) {
                                continue;
                            }
                            if (team.getTeam() != data.getComponent(other, TeamComponent.class).getTeam()) {
                                PositionComponent otherPosition = data.getComponent(other, PositionComponent.class);
                                int distance = MANHATTAN_HEURISTIC.estimateCost(new Position(positionComponent.getX(), positionComponent.getY()), new Position(otherPosition.getX(), otherPosition.getY()));
                                if (distance < preferredOpponentDistance.getDistanceScores().length) {
                                    value += preferredOpponentDistance.getDistanceScores()[distance];
                                }
                            }
                        }
                    }

                    AiHintAllyManhattanDistanceScoresComponent preferredAllyDistance = data.getComponent(entity, AiHintAllyManhattanDistanceScoresComponent.class);
                    if (preferredAllyDistance != null) {
                        for (int other : data.list(TeamComponent.class)) {
                            if (entity == other) {
                                continue;
                            }
                            if (team.getTeam() == data.getComponent(other, TeamComponent.class).getTeam()) {
                                PositionComponent otherPosition = data.getComponent(other, PositionComponent.class);
                                int distance = MANHATTAN_HEURISTIC.estimateCost(new Position(positionComponent.getX(), positionComponent.getY()), new Position(otherPosition.getX(), otherPosition.getY()));
                                if (distance < preferredAllyDistance.getDistanceScores().length) {
                                    value += preferredAllyDistance.getDistanceScores()[distance];
                                }
                            }
                        }
                    }
                }
                scores[teamIndex] += value;
                sum += value;
            }


            for (int i = 0; i < scores.length; i++) {
                scores[i] /= sum;
            }
        }
        return scores;
    }

    private static String humanReadableNanos(long nanos) {
        int count = 0;
        while (nanos > 10000 && count < 3) {
            nanos /= 1000;
            count++;
        }
        if (count == 3) {
            return nanos + "s";
        }
        return nanos + ("nμm".charAt(count) + "") + "s";
    }
}
