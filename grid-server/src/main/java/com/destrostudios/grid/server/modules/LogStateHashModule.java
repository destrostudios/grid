package com.destrostudios.grid.server.modules;

import com.destrostudios.gametools.network.server.modules.game.GameServerModule;
import com.destrostudios.gametools.network.shared.modules.NetworkModule;
import com.destrostudios.gametools.network.shared.modules.game.messages.GameActionRequest;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.esotericsoftware.kryonet.Connection;

public class LogStateHashModule extends NetworkModule {

    private final GameServerModule<GridGame, Action> gameModule;

    public LogStateHashModule(GameServerModule<GridGame, Action> gameModule) {
        this.gameModule = gameModule;
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameActionRequest) {
            GameActionRequest message = (GameActionRequest) object;
            System.out.println("Game state hash before action: " + Integer.toHexString(gameModule.getGame(message.game).state.getState().hashCode()));
        }
    }
}
