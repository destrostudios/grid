package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.destrostudios.grid.shared.PlayerInfo;
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
    // proxy the listeners since the game reference may change
    private final List<EventHandler<?>> preListeners = new ArrayList<>();
    private final List<EventHandler<?>> resolvedListeners = new ArrayList<>();

    @Override
    public boolean applyNextAction() {
        GridGame gridGame = getGame();
        // Ensure that the listeners are on the (potentially new) game instance
        for (EventHandler<? extends Event> listener : preListeners) {
            gridGame.removePreHandler(listener);
            gridGame.addPreHandler(listener.getEventClass(), listener);
        }
        for (EventHandler<? extends Event> listener : resolvedListeners) {
            gridGame.removeResolvedHandler(listener);
            gridGame.addResolvedHandler(listener.getEventClass(), listener);
        }
        return client.applyNextAction(gameId);
    }

    @Override
    public boolean triggeredHandlersInQueue() {
        GridGame gridGame = getGame();
        return gridGame.triggeredHandlersInQueue();
    }

    @Override
    public void triggerNextHandler() {
        GridGame gridGame = getGame();
        gridGame.triggerNextHandler();
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
    public void addPreHandler(EventHandler<? extends Event> handler) {
        preListeners.add(handler);
    }

    @Override
    public void addResolvedHandler(EventHandler<? extends Event> handler) {
        resolvedListeners.add(handler);
    }

    @Override
    public void removePreHandler(EventHandler<? extends Event> handler) {
        preListeners.remove(handler);
    }

    @Override
    public void removeResolvedHandler(EventHandler<? extends Event> handler) {
        resolvedListeners.remove(handler);
    }
}
