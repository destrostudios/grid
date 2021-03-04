package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverUtils;
import com.destrostudios.turnbasedgametools.bot.RolloutToEvaluation;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBot;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBotSettings;
import java.security.SecureRandom;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String... args) {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSSZ");
        Logger log = LoggerFactory.getLogger(com.destrostudios.grid.bot.Main.class);

        GridGame game = new GridGame();
        StartGameInfo gameInfo = StartGameInfo.getTestGameInfo();
        game.initGame(gameInfo);

        GridBotState botState = new GridBotState(game);
        MctsBot<GridBotState, Action, Team, SerializedGame> bot = createBot(botState);
        while (!GameOverUtils.getGameOverInfo(game.getWorld()).isGameIsOver()) {
            log.info("calculating...");
            List<Action> actions = bot.sortedActions(botState.activeTeam());
            game.registerAction(actions.get(0));
            while (game.triggeredHandlersInQueue()) {
                game.triggerNextHandler();
            }

            log.info("{}", actions);
        }
    }

    public static MctsBot<GridBotState, Action, Team, SerializedGame> createBot(GridBotState botState) {
        MctsBotSettings<GridBotState, Action> botSettings = new MctsBotSettings<>();
        botSettings.verbose = true;
        botSettings.maxThreads = 2;
        botSettings.strength = 100;
        botSettings.evaluation = new RolloutToEvaluation<>(new SecureRandom(), 10, Main::eval)::evaluate;

        return new MctsBot<>(new GridBotService(), botState, botSettings);
    }

    private static float[] eval(GridBotState s) {
        float[] scores = new float[s.getTeams().size()];
        if (s.isGameOver()) {
            GameOverUtils.GameOverInfo gameOverInfo = GameOverUtils.getGameOverInfo(s.game.getWorld());
            int winningTeam = gameOverInfo.getWinningTeamEntity();
            int teamIndex = s.getTeams().indexOf(new Team(winningTeam));
            scores[teamIndex] = 1;
        } else {
            float sum = 0;
            for (int entity : s.game.getWorld().list(TeamComponent.class)) {
                Team team = new Team(s.game.getWorld().getComponent(entity, TeamComponent.class).getTeam());
                int teamIndex = s.getTeams().indexOf(team);
                HealthPointsComponent health = s.game.getWorld().getComponent(entity, HealthPointsComponent.class);
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
}
