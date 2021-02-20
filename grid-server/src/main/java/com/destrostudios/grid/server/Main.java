package com.destrostudios.grid.server;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.authtoken.JwtService;
import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.network.KryoStartGameInfo;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverUtils;
import com.destrostudios.turnbasedgametools.network.server.ToolsServer;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameStartServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.LobbyServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.ServerGameData;
import com.destrostudios.turnbasedgametools.network.server.modules.jwt.JwtServerModule;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.destrostudios.turnbasedgametools.network.shared.modules.NetworkModule;
import com.destrostudios.turnbasedgametools.network.shared.modules.game.messages.GameActionRequest;
import com.destrostudios.turnbasedgametools.network.shared.modules.jwt.messages.Login;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        Log.DEBUG();
        Log.info(new Date().toString());// time reference for kryo logs
        System.err.println("WARNING: Using jwt service without validation.");
        JwtService jwtService = new NoValidateJwtService();

        System.out.println("Unsafe access warnings are a known issue, see: https://github.com/EsotericSoftware/kryonet/issues/154");

        NetworkGridService gameService = new NetworkGridService(true);
        Server kryoServer = new Server(10_000_000, 10_000_000);
        JwtServerModule jwtModule = new JwtServerModule(jwtService, kryoServer::getConnections);
        GameServerModule<GridGame, Action> gameModule = new GameServerModule<>(gameService, kryoServer::getConnections);
        LobbyServerModule<StartGameInfo> lobbyModule = new LobbyServerModule<>(KryoStartGameInfo::initialize, kryoServer::getConnections);
        GameStartServerModule<StartGameInfo> gameStartModule = new GameStartServerModule<>(KryoStartGameInfo::initialize) {
            @Override
            public void startGameRequest(Connection connection, StartGameInfo startGameInfo) {
                UUID gameId = UUID.randomUUID();
                GridGame gridGame = new GridGame();
                gridGame.initGame(startGameInfo);

                lobbyModule.listGame(gameId, startGameInfo);
                gameModule.registerGame(new ServerGameData<>(gameId, gridGame, new SecureRandom()));

                for (Connection other : kryoServer.getConnections()) {
                    JwtAuthenticationUser user = jwtModule.getUser(other.getID());
                    if (user != null && isUserInGame(startGameInfo, user)) {
                        gameModule.join(other, gameId);
                    }
                }
            }
        };
        NetworkModule gameOverModule = new NetworkModule() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GameActionRequest) {
                    GameActionRequest message = (GameActionRequest) object;
                    ServerGameData<GridGame> game = gameModule.getGame(message.game);
                    GameOverUtils.GameOverInfo gameOverInfo = GameOverUtils.getGameOverInfo(game.state.getWorld());
                    if (gameOverInfo.isGameIsOver()) {
                        lobbyModule.unlistGame(game.id);
                        gameModule.unregisterGame(game.id);
                    }
                }
            }
        };
        NetworkModule autoRejoinModule = new NetworkModule() {
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
                        if (isUserInGame(startGameInfo, user)) {
                            gameModule.join(connection, entry.getKey());
                        }
                    }
                }
            }
        };

        ToolsServer server = new ToolsServer(kryoServer, jwtModule, gameModule, lobbyModule, gameStartModule, gameOverModule, autoRejoinModule);
        server.start(NetworkUtil.PORT);

        System.out.println("Server started.");
    }

    private static boolean isUserInGame(StartGameInfo startGameInfo, JwtAuthenticationUser user) {
        return Stream.concat(startGameInfo.getTeam1().stream(), startGameInfo.getTeam2().stream())
                .anyMatch(p -> p.getId() == user.id);
    }
}
