package com.destrostudios.grid.server;

import com.destrostudios.authtoken.JwtAuthentication;
import com.destrostudios.authtoken.JwtService;
import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.network.messages.Identify;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.server.GamesServer;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        System.err.println("WARNING: Using jwt service without validation.");
        JwtService jwtService = new NoValidateJwtService();

        System.out.println("Unsafe access warnings are a known issue, see: https://github.com/EsotericSoftware/kryonet/issues/154");
        NetworkGridService gameService = new NetworkGridService();
        GamesServer<Game, ComponentUpdateEvent<?>> server = new GamesServer<>(NetworkUtil.PORT, gameService);
        UUID gameId = server.startNewGame();

        Map<Integer, PlayerInfo> connectionToPlayer = new ConcurrentHashMap<>();
        server.addConnectionListener(new Listener() {
            @Override
            public void connected(Connection connection) {
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Identify) {
                    Identify message = (Identify) object;
                    JwtAuthentication authentication = jwtService.decode(message.getJwt());
                    PlayerInfo player = new PlayerInfo((int) authentication.user.id, authentication.user.login);
                    connectionToPlayer.put(connection.getID(), player);
                    System.out.println("connection " + connection.getID() + " mapped to player " + player.getId() + " " + player.getLogin());
                    server.join(connection, gameId);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                connectionToPlayer.remove(connection.getID());
            }
        });
        System.out.println("Server started.");
    }
}
