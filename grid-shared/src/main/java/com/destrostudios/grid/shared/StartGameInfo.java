package com.destrostudios.grid.shared;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartGameInfo {

    private List<PlayerInfo> team1;
    private List<PlayerInfo> team2;
    private String mapName;

    public static StartGameInfo getTestGameInfo() {
        StartGameInfo startGameInfo = new StartGameInfo();

        LinkedList<PlayerInfo> team1 = new LinkedList<>();
        team1.add(new PlayerInfo(1, "destroflyer", "alice"));
        startGameInfo.team1 = team1;

        LinkedList<PlayerInfo> team2 = new LinkedList<>();
        team2.add(new PlayerInfo(2, "Etherblood", "iop"));
        startGameInfo.team2 = team2;

        startGameInfo.mapName = Maps.getRandomMapName();

        return startGameInfo;
    }
}
