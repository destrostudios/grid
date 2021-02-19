package com.destrostudios.grid.client.maps;

public class Maps {

    public static Map get(String name) {
        switch (name) {
            case "island": return new Map_Island();
            case "desert": return new Map_Desert();
            case "arctic": return new Map_Arctic();
        }
        throw new IllegalArgumentException(name);
    }
}
