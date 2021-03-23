package com.destrostudios.grid.client.gameproxy;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.components.character.NextTurnComponent;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientGameProxy implements GameProxy {

    private final UUID gameId;
    private final JwtAuthenticationUser jwtAuthenticationUser;
    private final GameClientModule<GridGame, Action> gameClientModule;
    private StartGameInfo startGameInfo;
    private GridGame game;
    // proxy the listeners since the game reference may change
    private final Map<Class<? extends Event>, EventHandler<?>> preListeners = new LinkedHashMap<>();
    private final Map<Class<? extends Event>, EventHandler<?>> resolvedListeners = new LinkedHashMap<>();

    public ClientGameProxy(UUID gameId, JwtAuthenticationUser jwtAuthenticationUser, GameClientModule<GridGame, Action> gameClientModule, LobbyClientModule<StartGameInfo> lobbyClientModule) {
        this.gameId = gameId;
        this.jwtAuthenticationUser = jwtAuthenticationUser;
        this.gameClientModule = gameClientModule;
        startGameInfo = lobbyClientModule.getListedGames().get(gameId);
    }

    @Override
    public StartGameInfo getStartGameInfo() {
        return startGameInfo;
    }

    @Override
    public boolean applyNextAction() {
        if (triggeredHandlersInQueue()) {
            return false;
        }
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
        int hash = gridGame.getState().hashCode();
        boolean actionWasApplied = gameClientModule.applyNextAction(gameId);
        if (actionWasApplied) {
            System.out.println("Game state hash before action: " + Integer.toHexString(hash));
        }
        return actionWasApplied;
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
        // Network library returns null as soon as the game ended - Caching it inside this getter also covers desync
        GridGame currentGame = gameClientModule.getJoinedGame(gameId).getState();
        if (currentGame != null) {
            game = currentGame;
        }
        return game;
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
                .sorted(Comparator.comparing(entity -> !data.hasComponents(entity, NextTurnComponent.class)))
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
