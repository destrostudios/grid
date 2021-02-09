package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.grid.update.eventbus.Listener;
import java.util.List;

public class SimpleGameProxy implements GameProxy {

    private final Game game;
    private boolean updated = false;

    public SimpleGameProxy(Game game) {
        this.game = game;
    }

    @Override
    public boolean update() {
        boolean result = updated;
        updated = false;
        return result;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void requestAction(ComponentUpdateEvent<?> action) {
        game.update(action);
        updated = true;
    }

    @Override
    public Integer getPlayerEntity() {
        List<Integer> list = game.getWorld().list(RoundComponent.class);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void addListener(Listener<?> listener) {
        game.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        game.removeOwnListener(listener);
    }
}
