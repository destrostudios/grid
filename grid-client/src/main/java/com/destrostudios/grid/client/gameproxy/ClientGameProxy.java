package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientGameProxy implements GameProxy {

    private final UUID gameId;
    private final JwtAuthenticationUser jwtAuthenticationUser;
    private final GameClientModule<GridGame, Action> gameClientModule;
    private final LobbyClientModule<StartGameInfo> lobbyClientModule;
    // proxy the listeners since the game reference may change
    private final Map<Class<? extends Event>, EventHandler<?>> preListeners = new LinkedHashMap<>();
    private final Map<Class<? extends Event>, EventHandler<?>> resolvedListeners = new LinkedHashMap<>();

    @Override
    public StartGameInfo getStartGameInfo() {
        return lobbyClientModule.getListedGames().get(gameId);
    }

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
        return gameClientModule.applyNextAction(gameId);
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
        return gameClientModule.getJoinedGame(gameId).getState();
    }

    @Override
    public void requestAction(Action action) {
        gameClientModule.sendAction(gameId, action);
    }

    @Override
    public Integer getPlayerEntity() {
        EntityData data = getGame().getData();
        List<Integer> list = data.list(PlayerComponent.class);
        Integer playerEntity = list.stream()
                .filter(entity -> data.hasComponents(entity, PlayerComponent.class))
                .filter(entity -> data.getComponent(entity, NameComponent.class).getName().equals(jwtAuthenticationUser.login)) // TODO: use Id instead
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

    @Override
    public void cleanupGame() {
        gameClientModule.removeJoinedGame(gameId);
    }
}
