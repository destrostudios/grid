package com.destrostudios.grid.shared;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class StartGameInfo {

    private List<PlayerInfo> team1;
    private List<PlayerInfo> team2;
    private String mapName;

    public static StartGameInfo getTestGameInfo() {
        StartGameInfo startGameInfo = new StartGameInfo();

        LinkedList<PlayerInfo> team1 = new LinkedList<>();
        team1.add(new PlayerInfo(1, "destroflyer"));
        startGameInfo.team1 = team1;

        LinkedList<PlayerInfo> team2 = new LinkedList<>();
        team2.add(new PlayerInfo(2, "Etherblood"));
        startGameInfo.team2 = team2;

        String[] mapNames = new String[]{ "island", "desert", "arctic" };
        startGameInfo.mapName = mapNames[(int) (Math.random() * mapNames.length)];

        return startGameInfo;
    }
}
