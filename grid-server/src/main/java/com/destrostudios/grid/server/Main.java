package com.destrostudios.grid.server;

import com.destrostudios.authtoken.JwtAuthentication;
import com.destrostudios.authtoken.JwtService;
import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.network.messages.Identify;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.turnbasedgametools.network.server.ToolsServer;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.ServerGameData;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.destrostudios.turnbasedgametools.network.shared.modules.NetworkModule;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        Log.DEBUG();
        System.err.println("WARNING: Using jwt service without validation.");
        JwtService jwtService = new NoValidateJwtService();

        System.out.println("Unsafe access warnings are a known issue, see: https://github.com/EsotericSoftware/kryonet/issues/154");
        NetworkGridService gameService = new NetworkGridService(true);
        Server kryoServer = new Server(10_000_000, 10_000_000);
        GameServerModule<GridGame, Action> gameModule = new GameServerModule<>(gameService, kryoServer::getConnections);
        NetworkModule identifyAutostartModule = new NetworkModule() {
            private final Map<Integer, PlayerInfo> connectionToPlayer = new ConcurrentHashMap<>();

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Identify) {
                    Identify message = (Identify) object;
                    JwtAuthentication authentication = jwtService.decode(message.getJwt());
                    PlayerInfo player = new PlayerInfo((int) authentication.user.id, authentication.user.login);
                    connectionToPlayer.put(connection.getID(), player);
                    System.out.println("connection " + connection.getID() + " mapped to player " + player.getId() + " " + player.getLogin());
                    for (ServerGameData<GridGame, Action> game : gameModule.getGames()) {
                        gameModule.join(connection, game.id);
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                connectionToPlayer.remove(connection.getID());
            }
        };
        ToolsServer server = new ToolsServer(kryoServer, gameModule, identifyAutostartModule);
        server.start(NetworkUtil.PORT);

        gameModule.startNewGame();
        System.out.println("Server started.");
    }
}
