package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.eventbus.Listener;
import com.destrostudios.turnbasedgametools.network.client.GamesClient;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ClientGameProxy implements GameProxy {

    private final UUID gameId;
    private final PlayerInfo player;
    private final GamesClient<GridGame, Action> client;
    private final List<Listener<?>> listeners = new ArrayList<>();// proxy the listeners since the game reference may change

    @Override
    public boolean update() {
        GridGame gridGame = getGame();
        // add all listeners to current game-state reference
        for (Listener<?> listener : listeners) {
            gridGame.addListener(listener);
        }
        boolean updated = client.updateGame(gameId);
        // and remove them again after the update
        for (Listener listener : listeners) {
            gridGame.removeListener(listener);
        }
        return updated;
    }

    @Override
    public GridGame getGame() {
        return client.getGame(gameId).getState();
    }

    @Override
    public void requestAction(Action action) {
        client.sendAction(gameId, action);
    }

    @Override
    public Integer getPlayerEntity() {
        EntityWorld world = getGame().getWorld();
        List<Integer> list = world.list(PlayerComponent.class);
        Integer playerEntity = list.stream()
                .filter(entity -> world.getComponent(entity, PlayerComponent.class).get().getName().equals(player.getLogin())) // TODO: use Id instead
                .findFirst().orElse(null);
        return playerEntity;
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
