package com.destrostudios.grid.server.modules;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.random.MutableRandomProxy;
import com.destrostudios.grid.server.Util;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.GameStartServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.LobbyServerModule;
import com.destrostudios.turnbasedgametools.network.server.modules.game.ServerGameData;
import com.destrostudios.turnbasedgametools.network.server.modules.jwt.JwtServerModule;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.function.Consumer;

public class GridStartModule extends GameStartServerModule<StartGameInfo> {
    private final Server kryoServer;
    private final JwtServerModule jwtModule;
    private final GameServerModule<GridGame, Action> gameModule;
    private final LobbyServerModule<StartGameInfo> lobbyModule;

    public GridStartModule(Consumer<Kryo> registerParams, Server kryoServer, JwtServerModule jwtModule, GameServerModule<GridGame, Action> gameModule, LobbyServerModule<StartGameInfo> lobbyModule) {
        super(registerParams);
        this.kryoServer = kryoServer;
        this.jwtModule = jwtModule;
        this.gameModule = gameModule;
        this.lobbyModule = lobbyModule;
    }

    @Override
    public void startGameRequest(Connection connection, StartGameInfo startGameInfo) {
        UUID gameId = UUID.randomUUID();
        GridGame gridGame = new GridGame(new MutableRandomProxy(new SecureRandom()::nextInt));
        gridGame.initGame(startGameInfo);

        lobbyModule.listGame(gameId, startGameInfo);
        gameModule.registerGame(new ServerGameData<>(gameId, gridGame, new SecureRandom()));

        for (Connection other : kryoServer.getConnections()) {
            JwtAuthenticationUser user = jwtModule.getUser(other.getID());
            if (user != null && Util.isUserInGame(startGameInfo, user)) {
                gameModule.join(other, gameId);
            }
        }
    }
}
