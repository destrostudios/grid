package com.destrostudios.grid.network;

import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.esotericsoftware.kryo.Kryo;
import java.util.LinkedList;

public class KryoStartGameInfo {

    public static void initialize(Kryo kryo) {
        kryo.register(StartGameInfo.class);
        kryo.register(PlayerInfo.class);
        kryo.register(LinkedList.class);// used by StartGameInfo
    }
}
