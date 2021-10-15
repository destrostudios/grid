package com.destrostudios.grid.server.modules;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverInfo;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.LobbyServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.ServerGameData;
import com.destrostudios.turnbasedgametools.network.shared.modules.NetworkModule;
import com.destrostudios.turnbasedgametools.network.shared.modules.game.messages.GameActionRequest;
import com.esotericsoftware.kryonet.Connection;

public class GameOverModule extends NetworkModule {

    private final LobbyServerModule<StartGameInfo> lobbyModule;
    private final GameServerModule<GridGame, Action> gameModule;

    public GameOverModule(LobbyServerModule<StartGameInfo> lobbyModule, GameServerModule<GridGame, Action> gameModule) {
        this.lobbyModule = lobbyModule;
        this.gameModule = gameModule;
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameActionRequest) {
            GameActionRequest message = (GameActionRequest) object;
            ServerGameData<GridGame> game = gameModule.getGame(message.game);

            GameOverInfo gameOverInfo = game.state.getGameOverInfo();
            if (gameOverInfo.isGameIsOver()) {
                lobbyModule.unlistGame(game.id);
                gameModule.unregisterGame(game.id);
            }
        }
    }
}
