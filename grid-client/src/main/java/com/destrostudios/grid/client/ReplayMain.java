package com.destrostudios.grid.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.destrostudios.grid.client.gameproxy.ReplayGameProxy;
import com.destrostudios.grid.client.replay.GameReplay;
import com.destrostudios.grid.client.replay.ReplayIO;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

public class ReplayMain {

    public static void main(String... args) throws IOException {
        ClientApplication clientApplication = Main.startApplication((ToolsClient) null, getTestJwt(0));
        GameReplay replay = ReplayIO.read(Paths.get("replay.json"));
        clientApplication.enqueue(() -> clientApplication.startGame(new ReplayGameProxy(new NetworkGridService(false), replay)));
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
