package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.NameComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class ClientGameProxy implements GameProxy {

    private final UUID gameId;
    private final PlayerInfo player;
    private final GameClientModule<GridGame, Action> client;
    // proxy the listeners since the game reference may change
    private final Map<Class<? extends Event>, EventHandler<?>> preListeners = new LinkedHashMap<>();
    private final Map<Class<? extends Event>, EventHandler<?>> resolvedListeners = new LinkedHashMap<>();

    @Override
    public boolean applyNextAction() {
        GridGame gridGame = getGame();
        // Ensure that the listeners are on the (potentially new) game instance
        for (Map.Entry<Class<? extends Event>, EventHandler<?>> entry : preListeners.entrySet()) {
            gridGame.removePreHandler(entry.getKey(), entry.getValue());
            gridGame.addPreHandler(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Class<? extends Event>, EventHandler<?>> entry : resolvedListeners.entrySet()) {
            gridGame.removeResolvedHandler(entry.getKey(), entry.getValue());
            gridGame.addResolvedHandler(entry.getKey(), entry.getValue());
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
                .filter(entity -> world.hasComponents(entity, PlayerComponent.class))
                .filter(entity -> world.getComponent(entity, NameComponent.class).get().getName().equals(player.getLogin())) // TODO: use Id instead
                .findFirst().orElse(null);
        return playerEntity;
    }

    @Override
    public void addPreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        preListeners.put(eventClass, handler);
    }

    @Override
    public void addResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        resolvedListeners.put(eventClass, handler);
    }

    @Override
    public void removePreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        preListeners.remove(eventClass, handler);
    }

    @Override
    public void removeResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        resolvedListeners.remove(eventClass, handler);
    }
}
