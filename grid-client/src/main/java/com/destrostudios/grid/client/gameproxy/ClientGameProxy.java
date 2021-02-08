package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.update.eventbus.Listener;
import com.destrostudios.turnbasedgametools.network.client.GamesClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientGameProxy implements GameProxy {

    private final UUID gameId;
    private final GamesClient<Game, ComponentUpdateEvent<?>> client;
    private final List<Listener<?>> listeners = new ArrayList<>();// proxy the listeners since the game reference may change

    @Override
    public boolean update() {
        Game game = getGame();
        // add all listeners to current game-state reference
        for (Listener<?> listener : listeners) {
            game.addListener(listener);
        }
        boolean updated = client.updateGame(gameId);
        // and remove them again after the update
        for (Listener listener : listeners) {
            game.removeOwnListener(listener);
        }
        return updated;
    }

    @Override
    public Game getGame() {
        return client.getGame(gameId).getState();
    }

    @Override
    public void requestAction(ComponentUpdateEvent<?> action) {
        client.sendAction(gameId, action);
    }

    @Override
    public Integer getPlayerEntity() {
        return client.getGame(gameId).getTags().stream()
                .filter(x -> x instanceof Integer)
                .map(x -> (Integer) x)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addListener(Listener<?> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener<?> listener) {
        listeners.remove(listener);
    }

}
