package com.destrostudios.grid.shared;

public class Characters {

    public static String[] CHARACTER_NAMES = new String[]{
        "aland", "alice", "dosaz", "dwarf_warrior", "elven_archer", "garmon", "scarlet", "tristan", "iop"
    };

    public static String getRandomCharacterName() {
        return CHARACTER_NAMES[(int) (Math.random() * CHARACTER_NAMES.length)];
    }
}
