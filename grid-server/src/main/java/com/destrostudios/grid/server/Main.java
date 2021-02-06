package com.destrostudios.grid.server;

import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.server.GamesServer;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Unsafe access warnings are a known issue, see: https://github.com/EsotericSoftware/kryonet/issues/154");
        NetworkGridService gameService = new NetworkGridService();
        GamesServer<Game, ComponentUpdateEvent<?>> server = new GamesServer<>(NetworkUtil.PORT, gameService);
        UUID gameId = server.startNewGame();
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                server.spectate(connection, gameId);
            }
        });
        System.out.println("Server started.");
    }
}
