package com.destrostudios.grid.bot;

import com.destrostudios.gametools.bot.BotActionReplay;
import com.destrostudios.gametools.bot.RolloutToEvaluation;
import com.destrostudios.gametools.bot.mcts.MctsBot;
import com.destrostudios.gametools.bot.mcts.MctsBotSettings;
import com.destrostudios.gametools.grid.DistanceMetric;
import com.destrostudios.gametools.grid.ManhattanDistance;
import com.destrostudios.gametools.grid.Position;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.ai.AiHintCharacterComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final DistanceMetric MANHATTAN_DISTANCE = new ManhattanDistance();

    // ideally we would calculate preferred distances from available skills instead of hardcoding them per character
    private static final Map<String, ManhattanDistanceScore> OPPONENT_DISTANCE_SCORES = Map.of(
            "iop", new ManhattanDistanceScore(new float[]{
                    0f,
                    0.02f,
                    0.016f,
                    0.013f,
                    0.01f,
                    0.007f,
                    0.005f,
                    0.003f,
                    0.001f})
    );
    private static final Map<String, ManhattanDistanceScore> ALLY_DISTANCE_SCORES = Map.of(
            "alice", new ManhattanDistanceScore(new float[]{
                    0f,
                    0.01f,
                    0.009f,
                    0.008f,
                    0.007f,
                    0.006f,
                    0.005f,
                    0.004f,
                    0.003f,
                    0.002f,
                    0.001f})
    );

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
        long gameStartNanos = System.nanoTime();
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
        long gameDurationNanos = System.nanoTime() - gameStartNanos;
        log.info("Finished full game after {}.", humanReadableNanos(gameDurationNanos));
    }

    public static MctsBot<GridBotState, Action, Team, SerializedGame> createBot(int strength) {
        MctsBotSettings<GridBotState, Action> botSettings = new MctsBotSettings<>();
        botSettings.verbose = true;
        botSettings.maxThreads = 3;
        botSettings.strength = strength;
        botSettings.evaluation = new RolloutToEvaluation<>(new Random(), 5, Main::eval)::evaluate;

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
                    AiHintCharacterComponent characterComponent = data.getComponent(entity, AiHintCharacterComponent.class);
                    if (characterComponent != null) {

                        ManhattanDistanceScore preferredOpponentDistance = OPPONENT_DISTANCE_SCORES.get(characterComponent.getName());
                        if (preferredOpponentDistance != null) {
                            for (int other : data.list(TeamComponent.class)) {
                                if (entity == other) {
                                    continue;
                                }
                                if (team.getTeam() != data.getComponent(other, TeamComponent.class).getTeam()) {
                                    PositionComponent otherPosition = data.getComponent(other, PositionComponent.class);
                                    int distance = MANHATTAN_DISTANCE.distanceBetween(new Position(positionComponent.getX(), positionComponent.getY()), new Position(otherPosition.getX(), otherPosition.getY()));
                                    if (distance < preferredOpponentDistance.getDistanceScores().length) {
                                        value += preferredOpponentDistance.getDistanceScores()[distance];
                                    }
                                }
                            }
                        }

                        ManhattanDistanceScore preferredAllyDistance = ALLY_DISTANCE_SCORES.get(characterComponent.getName());
                        if (preferredAllyDistance != null) {
                            for (int other : data.list(TeamComponent.class)) {
                                if (entity == other) {
                                    continue;
                                }
                                if (team.getTeam() == data.getComponent(other, TeamComponent.class).getTeam()) {
                                    PositionComponent otherPosition = data.getComponent(other, PositionComponent.class);
                                    int distance = MANHATTAN_DISTANCE.distanceBetween(new Position(positionComponent.getX(), positionComponent.getY()), new Position(otherPosition.getX(), otherPosition.getY()));
                                    if (distance < preferredAllyDistance.getDistanceScores().length) {
                                        value += preferredAllyDistance.getDistanceScores()[distance];
                                    }
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
