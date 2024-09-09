package com.destrostudios.grid.bot;

import com.destrostudios.gametools.bot.BotGameService;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;

public class GridBotService implements BotGameService<GridBotState, Action, Team, SerializedGame> {

    @Override
    public SerializedGame serialize(GridBotState gridBotState) {
        return new SerializedGame(gridBotState.game.getWorld());
    }

    @Override
    public GridBotState deserialize(SerializedGame data, GridBotState target) {
        GridGame game = new GridGame();
        game.initializeGame(data.world);
        return new GridBotState(game);
    }
}
