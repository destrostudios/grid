package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SimpleGameProxy implements GameProxy {

    private final GridGame gridGame;
    private final Queue<Action> actions;

    public SimpleGameProxy(GridGame gridGame) {
        this.gridGame = gridGame;
        actions = new LinkedList<>();
    }

    @Override
    public boolean applyNextAction() {
        Action action = actions.poll();
        if (action != null) {
            gridGame.registerAction(action);
            return true;
        }
        return false;
    }

    @Override
    public boolean triggeredHandlersInQueue() {
        return gridGame.triggeredHandlersInQueue();
    }

    @Override
    public void triggerNextHandler() {
        gridGame.triggerNextHandler();
    }

    @Override
    public GridGame getGame() {
        return gridGame;
    }

    @Override
    public void requestAction(Action action) {
        actions.add(action);
    }

    @Override
    public Integer getPlayerEntity() {
        List<Integer> list = gridGame.getWorld().list(RoundComponent.class);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void addPreHandler(EventHandler<? extends Event> handler) {
        gridGame.addPreHandler(handler.getEventClass(), handler);
    }

    @Override
    public void removePreHandler(EventHandler<? extends Event> handler) {
        gridGame.removePreHandler(handler);
    }

    @Override
    public void addResolvedHandler(EventHandler<? extends Event> handler) {
        gridGame.addResolvedHandler(handler.getEventClass(), handler);
    }

    @Override
    public void removeResolvedHandler(EventHandler<? extends Event> handler) {
        gridGame.removeResolvedHandler(handler);
    }
}
