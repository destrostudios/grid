package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.turnbasedgametools.bot.BotGameService;

public class GridBotService implements BotGameService<GridBotState, Action, Team, SerializedGame> {

    @Override
    public SerializedGame serialize(GridBotState gridBotState) {
        return new SerializedGame(gridBotState.game.getState());
    }

    @Override
    public GridBotState deserialize(SerializedGame data) {
        GridGame game = new GridGame();
        game.intializeGame(data.value);
        return new GridBotState(game);
    }
}
