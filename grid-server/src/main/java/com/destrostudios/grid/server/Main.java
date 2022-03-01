package com.destrostudios.grid.server;

import com.destrostudios.authtoken.JwtService;
import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.gametools.network.server.ToolsServer;
import com.destrostudios.gametools.network.server.modules.game.GameServerModule;
import com.destrostudios.gametools.network.server.modules.game.GameStartServerModule;
import com.destrostudios.gametools.network.server.modules.game.LobbyServerModule;
import com.destrostudios.gametools.network.server.modules.jwt.JwtServerModule;
import com.destrostudios.gametools.network.shared.NetworkUtil;
import com.destrostudios.gametools.network.shared.modules.NetworkModule;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.network.KryoStartGameInfo;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.server.modules.AutoRejoinModule;
import com.destrostudios.grid.server.modules.GameOverModule;
import com.destrostudios.grid.server.modules.GridStartModule;
import com.destrostudios.grid.server.modules.LogStateHashModule;
import com.destrostudios.grid.shared.StartGameInfo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;

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
        GameStartServerModule<StartGameInfo> gameStartModule = new GridStartModule(KryoStartGameInfo::initialize, kryoServer, jwtModule, gameModule, lobbyModule);
        NetworkModule gameOverModule = new GameOverModule(lobbyModule, gameModule);
        NetworkModule autoRejoinModule = new AutoRejoinModule(jwtModule, lobbyModule, gameModule);
        NetworkModule logStateHashModule = new LogStateHashModule(gameModule);

        ToolsServer server = new ToolsServer(kryoServer, logStateHashModule, jwtModule, gameModule, lobbyModule, gameStartModule, gameOverModule, autoRejoinModule);
        server.start(NetworkUtil.PORT);

        System.out.println("Server started.");
    }
}
