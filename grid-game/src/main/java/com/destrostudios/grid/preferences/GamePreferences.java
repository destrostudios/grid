package com.destrostudios.grid.preferences;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamePreferences {

    private float fps;
    private boolean gameRunning;

    public GamePreferences() {
        this.fps = 30f;
        this.gameRunning = true;
    }

}
