package com.destrostudios.grid.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class LocalhostMain1 {

    public static void main(String... args) throws IOException, InterruptedException {
        Log.DEBUG();
        StartGameInfo gameInfo = StartGameInfo.getTestGameInfo();
        PlayerInfo playerInfo = gameInfo.getTeam1().get(0);
        Map<String, ?> user = Map.of("id", playerInfo.getId(), "login", playerInfo.getLogin());
        String jwt = JWT.create()
                .withIssuedAt(new Date())
                .withClaim("user", user)
                .sign(Algorithm.none());
        Main.startGame(Main.getClientProxy("localhost", jwt));
    }
}
