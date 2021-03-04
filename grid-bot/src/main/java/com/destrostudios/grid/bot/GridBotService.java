package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.Component;
import com.destrostudios.turnbasedgametools.bot.BotGameService;
import java.util.List;
import java.util.Map;

public class GridBotService implements BotGameService<GridBotState, Action, Team, Map<Integer, List<Component>>> {

    @Override
    public Map<Integer, List<Component>> serialize(GridBotState gridBotState) {
        return Map.copyOf(gridBotState.game.getWorld().getWorld());
    }

    @Override
    public GridBotState deserialize(Map<Integer, List<Component>> data) {
        GridGame game = new GridGame();
        game.getWorld().getWorld().putAll(data);
        return new GridBotState(game);
    }
}
