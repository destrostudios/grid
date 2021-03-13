package com.destrostudios.grid.shared;

public class Maps {

    public static String[] MAP_NAMES = new String[] {
        "DestroMap", "EtherMap", "IceMap", "EgoMap"
    };

    public static String getRandomMapName() {
        return MAP_NAMES[(int) (Math.random() * MAP_NAMES.length)];
    }
}
