package com.destrostudios.grid.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class LocalhostMain {

    public static void main(String... args) throws IOException, InterruptedException {
        Log.DEBUG();
        Map<String, ?> user = Map.of("id", 1, "login", "destroflyer");
        String jwt = JWT.create()
                .withIssuedAt(new Date())
                .withClaim("user", user)
                .sign(Algorithm.none());
        Main.startGame(Main.getClientProxy("localhost", jwt));
    }
}
