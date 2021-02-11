package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.eventbus.events.NewEvent;
import com.destrostudios.grid.eventbus.handler.NewEventHandler;

public interface GameProxy {

    // intention of this interface is to hide the client from our gui code.
    // It will simplify supporting non-networked games later

    /**
     * Apply any game-state updates that are available
     *
     * @return whether the game-state was updated
     */
    boolean update();

    /**
     * WARNING:
     * The returned reference may be different from previously returned ones, eg. when the client reconnected after a desync.
     *
     * @return current game-state
     */
    GridGame getGame();

    void requestAction(Action action);

    Integer getPlayerEntity();

    <E extends NewEvent> void addListener(NewEventHandler<E> handler);

    <E extends NewEvent> void removeListener(NewEventHandler<E> handler);
}
