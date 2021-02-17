package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;

public interface GameProxy {

    // intention of this interface is to hide the client from our gui code.
    // It simplifies supporting non-networked games

    boolean applyNextAction();

    boolean triggeredHandlersInQueue();

    void triggerNextHandler();

    /**
     * WARNING:
     * The returned reference may be different from previously returned ones, eg. when the client reconnected after a desync.
     *
     * @return current game-state
     */
    GridGame getGame();

    void requestAction(Action action);

    Integer getPlayerEntity();

    void addPreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler);

    void removePreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler);

    void addResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler);

    void removeResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler);
}
