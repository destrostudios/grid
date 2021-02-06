package com.destrostudios.grid.preferences;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamePreferences {

    private float fps;
    private boolean gameRunning;
    private final int mapSizeX;
    private final int mapSizeY;

    public GamePreferences(int mapSizeX, int mapSizeY) {
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.fps = 30f;
        this.gameRunning = true;
    }

}
