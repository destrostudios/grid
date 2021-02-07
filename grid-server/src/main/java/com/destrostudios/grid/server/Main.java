package com.destrostudios.grid.server;

import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.server.GamesServer;
import com.destrostudios.turnbasedgametools.network.server.ServerGameData;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Unsafe access warnings are a known issue, see: https://github.com/EsotericSoftware/kryonet/issues/154");
        NetworkGridService gameService = new NetworkGridService();
        GamesServer<Game, ComponentUpdateEvent<?>> server = new GamesServer<>(NetworkUtil.PORT, gameService);
        UUID gameId = server.startNewGame();
        server.addConnectionListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                ServerGameData<Game, ComponentUpdateEvent<?>> game = server.getGame(gameId);
                int size = game.getConnectionTags().keySet().size();
                List<Integer> playerEntities = game.state.getWorld().list(PlayerComponent.class);
                if (size < playerEntities.size()) {
                    server.join(connection, gameId, Collections.singleton(playerEntities.get(size)));
                } else {
                    server.join(connection, gameId, Collections.emptySet());
                }
            }
        });
        System.out.println("Server started.");
    }
}
