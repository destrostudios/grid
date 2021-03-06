package com.destrostudios.grid.client.maps;

public class Maps {

    public static Map get(String name) {
        switch (name) {
            case "IceMap": return new Map_Icecold();
            case "DestroMap": return new Map_Destro();
            case "EtherMap": return new Map_Ether();
            case "EgoMap": return new Map_Ego();
        }
        throw new IllegalArgumentException(name);
    }
}
