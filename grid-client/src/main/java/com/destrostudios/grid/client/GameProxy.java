package com.destrostudios.grid.client;

import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.client.GamesClient;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameProxy {
    private final UUID gameId;
    private final GamesClient<Game, ComponentUpdateEvent<?>> client;

    public Game getGame() {
        return client.getGames().stream()
                .filter(x -> x.id.equals(gameId))
                .findAny().map(x -> x.state)
                .orElse(null);
    }

    public void applyAction(ComponentUpdateEvent<?> action) {
        client.sendAction(gameId, action);
    }
}
