package com.destrostudios.grid.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.client.gameproxy.SimpleGameProxy;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class SimpleMain {

    public static void main(String... args) throws IOException {
        ClientApplication clientApplication = Main.startApplication((ToolsClient) null, getTestJwt(0));
        GridGame gridGame = new GridGame();
        gridGame.initGame(StartGameInfo.getTestGameInfo());
        clientApplication.enqueue(() -> clientApplication.startGame(new SimpleGameProxy(gridGame)));
    }

    static String getTestJwt(int teamIndex) {
        StartGameInfo gameInfo = StartGameInfo.getTestGameInfo();
        PlayerInfo playerInfo = ((teamIndex == 0) ? gameInfo.getTeam1() : gameInfo.getTeam2()).get(0);
        Map<String, ?> user = Map.of("id", playerInfo.getId(), "login", playerInfo.getLogin());
        return JWT.create()
                .withIssuedAt(new Date())
                .withClaim("user", user)
                .sign(Algorithm.none());
    }
}
