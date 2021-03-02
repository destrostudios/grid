package com.destrostudios.grid.bot;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.turnbasedgametools.bot.BotGameService;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

public class GridBotService implements BotGameService<GridBotState, Action, Team> {

    @SneakyThrows
    @Override
    public void serialize(GridBotState gridBotState, OutputStream outputStream) {
        outputStream.write(gridBotState.game.getState().getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    @Override
    public GridBotState deserialize(InputStream inputStream) {
        GridGame game = new GridGame();
        game.intializeGame(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        return new GridBotState(game);
    }
}
