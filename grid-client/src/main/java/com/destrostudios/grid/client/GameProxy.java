package com.destrostudios.grid.client;

import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.grid.update.listener.ComponentUpdateListener;
import com.destrostudios.turnbasedgametools.network.client.GamesClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameProxy {
    // intention of this class is to hide the client from our gui code.
    // It will simplify supporting non-networked games later

    private final UUID gameId;
    private final GamesClient<Game, ComponentUpdateEvent<?>> client;
    private final List<ComponentUpdateListener<?>> listeners = new ArrayList<>();// proxy the listeners since the game reference may change

    /**
     * Apply any game-state updates that are available
     *
     * @return whether the game-state was updated
     */
    public boolean update() {
        Game game = getGame();
        // add all listeners to current game-state reference
        for (ComponentUpdateListener<?> listener : listeners) {
            game.addListener(listener);
        }
        boolean updated = client.updateGame(gameId);
        // and remove them again after the update
        for (ComponentUpdateListener<?> listener : listeners) {
            game.removeListener(listener);
        }
        return updated;
    }

    /**
     * WARNING:
     * The returned reference may be different from previously returned ones, eg. when the client reconnected after a desync.
     *
     * @return current game-state
     */
    public Game getGame() {
        return client.getGame(gameId).getState();
    }

    public void requestAction(ComponentUpdateEvent<?> action) {
        client.sendAction(gameId, action);
    }


    public void addListener(ComponentUpdateListener<?> listener) {
        listeners.add(listener);
    }

    public void removeListener(ComponentUpdateListener<?> listener) {
        listeners.remove(listener);
    }

}
