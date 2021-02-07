package com.destrostudios.grid.client;

import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.client.GamesClient;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameProxy {
    // intention of this class is to hide the client from our gui code,
    // this will later allow us to swap-in non-networked games without gui changes

    private final UUID gameId;
    private final GamesClient<Game, ComponentUpdateEvent<?>> client;

    public Game getGame() {
        return client.getGame(gameId).getState();
    }

    /**
     * Apply any game-state updates that are available
     *
     * @return whether the game-state was updated
     */
    public boolean update() {
        return client.updateGame(gameId);
    }

    public void requestAction(ComponentUpdateEvent<?> action) {
        client.sendAction(gameId, action);
    }
}
