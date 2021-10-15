package com.destrostudios.grid.server.modules;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.server.Util;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.LobbyServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.jwt.JwtServerModule;
import com.destrostudios.turnbasedgametools.network.shared.modules.NetworkModule;
import com.destrostudios.turnbasedgametools.network.shared.modules.jwt.messages.Login;
import com.esotericsoftware.kryonet.Connection;
import java.util.Map;
import java.util.UUID;

public class AutoRejoinModule extends NetworkModule {

    private final JwtServerModule jwtModule;
    private final LobbyServerModule<StartGameInfo> lobbyModule;
    private final GameServerModule<GridGame, Action> gameModule;

    public AutoRejoinModule(JwtServerModule jwtModule, LobbyServerModule<StartGameInfo> lobbyModule, GameServerModule<GridGame, Action> gameModule) {
        this.jwtModule = jwtModule;
        this.lobbyModule = lobbyModule;
        this.gameModule = gameModule;
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Login) {
            JwtAuthenticationUser user = jwtModule.getUser(connection.getID());
            if (user == null) {
                // unsuccessful login
                return;
            }
            for (Map.Entry<UUID, StartGameInfo> entry : lobbyModule.getGames().entrySet()) {
                StartGameInfo startGameInfo = entry.getValue();
                if (Util.isUserInGame(startGameInfo, user)) {
                    gameModule.join(connection, entry.getKey());
                }
            }
        }
    }
}
