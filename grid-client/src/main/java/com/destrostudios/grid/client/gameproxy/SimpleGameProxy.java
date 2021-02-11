package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.eventbus.events.NewEvent;
import com.destrostudios.grid.eventbus.handler.NewEventHandler;

import java.util.List;

public class SimpleGameProxy implements GameProxy {

    private final GridGame gridGame;
    private boolean updated = false;

    public SimpleGameProxy(GridGame gridGame) {
        this.gridGame = gridGame;
    }

    @Override
    public boolean update() {
        boolean result = updated;
        updated = false;
        return result;
    }

    @Override
    public GridGame getGame() {
        return gridGame;
    }

    @Override
    public void requestAction(Action action) {
        gridGame.registerAction(action);
        updated = true;
    }

    @Override
    public Integer getPlayerEntity() {
        List<Integer> list = gridGame.getWorld().list(RoundComponent.class);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public <E extends NewEvent> void addListener(NewEventHandler<E> handler) {
        gridGame.addListener(handler.getEventClass(), handler);
    }

    @Override
    public <E extends NewEvent> void removeListener(NewEventHandler<E> handler) {
        gridGame.removeInstantHandler(handler);
    }
}
