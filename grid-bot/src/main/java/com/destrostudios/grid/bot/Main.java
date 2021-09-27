package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverInfo;
import com.destrostudios.turnbasedgametools.bot.BotActionReplay;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBot;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBotSettings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String... args) {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSSZ");
        Logger log = LoggerFactory.getLogger(com.destrostudios.grid.bot.Main.class);

        int strength = 100;

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
            log.info("Finished after {} ({} / iteration).", humanReadableNanos(durationNanos), humanReadableNanos(durationNanos / strength));

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
        botSettings.maxThreads = 2;
        botSettings.strength = strength;
//        botSettings.evaluation = new RolloutToEvaluation<>(new SecureRandom(), 10, Main::eval)::evaluate;
        botSettings.evaluation = Main::eval;

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
            for (int entity : s.game.getData().list(TeamComponent.class)) {
                Team team = new Team(s.game.getData().getComponent(entity, TeamComponent.class).getTeam());
                int teamIndex = s.getTeams().indexOf(team);
                HealthPointsComponent health = s.game.getData().getComponent(entity, HealthPointsComponent.class);
                if (health != null) {
                    float value = health.getHealth();
                    scores[teamIndex] += value;
                    sum += value;
                }
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
