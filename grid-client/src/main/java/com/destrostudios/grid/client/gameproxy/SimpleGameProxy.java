package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.shared.StartGameInfo;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;

public class SimpleGameProxy implements GameProxy {

    @Getter
    private final StartGameInfo startGameInfo;
    private final GridGame gridGame;
    private final Queue<Action> actions;

    public SimpleGameProxy(StartGameInfo startGameInfo) {
        this.startGameInfo = startGameInfo;
        gridGame = new GridGame();
        gridGame.initGame(startGameInfo);
        actions = new LinkedList<>();
    }

    @Override
    public boolean applyNextAction() {
        if (triggeredHandlersInQueue()) {
            return false;
        }
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
        List<Integer> list = gridGame.getData().list(ActiveTurnComponent.class);
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
