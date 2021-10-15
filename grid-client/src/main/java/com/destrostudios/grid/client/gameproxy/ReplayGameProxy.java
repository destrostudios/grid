package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.client.replay.ActionReplay;
import com.destrostudios.grid.client.replay.GameReplay;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.random.MutableRandomProxy;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.modules.game.SlaveRandom;
import lombok.Getter;

public class ReplayGameProxy implements GameProxy {

    private final NetworkGridService gridService;
    private GridGame gridGame;
    @Getter
    private final GameReplay replay;
    private int next;

    public ReplayGameProxy(NetworkGridService gridService, GameReplay replay) {
        this.gridService = gridService;
        this.replay = replay;
        next = 0;
        gridGame = new GridGame(new MutableRandomProxy());
        gridGame.intializeGame(replay.getInitial());
    }

    @Override
    public StartGameInfo getStartGameInfo() {
        return replay.getStartGameInfo();
    }

    @Override
    public boolean applyNextAction() {
        if (triggeredHandlersInQueue()) {
            return false;
        }
        if (replay.getActions().size() < next) {
            return false;
        }
        ActionReplay action = replay.getActions().get(next++);
        if (action != null) {
            gridGame = gridService.applyAction(gridGame, action.action, new SlaveRandom(action.randomHistory));
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getPlayerEntity() {
        return null;
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
