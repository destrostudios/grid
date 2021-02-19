package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.RoundComponent;
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
    public void addPreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        gridGame.addPreHandler(eventClass, handler);
    }

    @Override
    public void removePreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        gridGame.removePreHandler(eventClass, handler);
    }

    @Override
    public void addResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        gridGame.addResolvedHandler(eventClass, handler);
    }

    @Override
    public void removeResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        gridGame.removeResolvedHandler(eventClass, handler);
    }

    @Override
    public void cleanupGame() {

    }
}
