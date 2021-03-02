package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBot;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBotSettings;
import java.util.List;

public class Main {

    public static void main(String... args) {
        GridGame game = new GridGame();
        StartGameInfo gameInfo = StartGameInfo.getTestGameInfo();
        game.initGame(gameInfo);

//        System.out.println(game.getState());

        GridBotState botState = new GridBotState(game);
        MctsBotSettings<GridBotState, Action> botSettings = new MctsBotSettings<>();
        botSettings.evaluation = s -> {
            float sum = 0;
            float[] scores = new float[s.getTeams().size()];
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
            return scores;
        };

        MctsBot<GridBotState, Action, Team> bot = new MctsBot<>(new GridBotService(), botState, botSettings);
        List<Action> actions = bot.sortedActions(botState.activeTeam());
        System.out.println(actions);
    }
}
