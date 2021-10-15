package com.destrostudios.grid.server;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.shared.StartGameInfo;
import java.util.stream.Stream;

public class Util {

    public static boolean isUserInGame(StartGameInfo startGameInfo, JwtAuthenticationUser user) {
        return Stream.concat(startGameInfo.getTeam1().stream(), startGameInfo.getTeam2().stream())
                .anyMatch(p -> p.getId() == user.id);
    }
}
