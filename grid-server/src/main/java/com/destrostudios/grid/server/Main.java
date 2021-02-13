package com.destrostudios.grid.server;

import com.destrostudios.authtoken.JwtService;
import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.server.ToolsServer;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.ServerGameData;
import com.destrostudios.turnbasedgametools.network.server.modules.jwt.JwtServerModule;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.destrostudios.turnbasedgametools.network.shared.modules.NetworkModule;
import com.destrostudios.turnbasedgametools.network.shared.modules.jwt.messages.Login;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Log.DEBUG();
        System.err.println("WARNING: Using jwt service without validation.");
        JwtService jwtService = new NoValidateJwtService();

        System.out.println("Unsafe access warnings are a known issue, see: https://github.com/EsotericSoftware/kryonet/issues/154");
        NetworkGridService gameService = new NetworkGridService(true);
        Server kryoServer = new Server(10_000_000, 10_000_000);
        GameServerModule<GridGame, Action, StartGameInfo> gameModule = new GameServerModule<>(gameService, kryoServer::getConnections);
        JwtServerModule jwtModule = new JwtServerModule(jwtService, kryoServer::getConnections);
        NetworkModule loginAutoJoinModule = new NetworkModule() {

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Login) {
                    for (ServerGameData<GridGame> game : gameModule.getGames()) {
                        gameModule.join(connection, game.id);
                    }
                }
            }
        };
        ToolsServer server = new ToolsServer(kryoServer, gameModule, jwtModule, loginAutoJoinModule);
        server.start(NetworkUtil.PORT);

        gameModule.startNewGame(StartGameInfo.getTestGameInfo());
        System.out.println("Server started.");
    }
}
