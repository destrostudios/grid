package com.destrostudios.grid.shared;

public class Characters {

    public static String[] CHARACTER_NAMES = new String[]{
            "cra", "iop"
    };

    public static String getRandomCharacterName() {
        return CHARACTER_NAMES[(int) (Math.random() * CHARACTER_NAMES.length)];
    }
}
