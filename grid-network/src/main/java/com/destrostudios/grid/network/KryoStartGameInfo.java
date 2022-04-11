package com.destrostudios.grid.network;

import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.ArrayList;
import java.util.List;

public class KryoStartGameInfo {

    public static void initialize(Kryo kryo) {
        Serializer<PlayerInfo> playerInfoSerializer = new Serializer<>() {
            @Override
            public void write(Kryo kryo, Output output, PlayerInfo object) {
                output.writeLong(object.getId());
                output.writeString(object.getLogin());
                output.writeString(object.getCharacterName());
            }

            @Override
            public PlayerInfo read(Kryo kryo, Input input, Class<PlayerInfo> type) {
                return new PlayerInfo(input.readLong(), input.readString(), input.readString());
            }
        };
        Serializer<List<PlayerInfo>> teamSerializer = new Serializer<>() {

            @Override
            public void write(Kryo kryo, Output output, List<PlayerInfo> object) {
                output.writeInt(object.size());
                for (int i = 0; i < object.size(); i++) {
                    kryo.writeObject(output, object.get(i), playerInfoSerializer);
                }
            }

            @Override
            public List<PlayerInfo> read(Kryo kryo, Input input, Class<List<PlayerInfo>> type) {
                int size = input.readInt();
                List<PlayerInfo> list = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    list.add(kryo.readObject(input, PlayerInfo.class, playerInfoSerializer));
                }
                return list;
            }
        };
        kryo.register(StartGameInfo.class, new Serializer<StartGameInfo>() {


            @Override
            public void write(Kryo kryo, Output output, StartGameInfo object) {
                output.writeString(object.getMapName());
                kryo.writeObject(output, object.getTeam1(), teamSerializer);
                kryo.writeObject(output, object.getTeam2(), teamSerializer);
            }

            @Override
            public StartGameInfo read(Kryo kryo, Input input, Class<StartGameInfo> type) {
                StartGameInfo startGameInfo = new StartGameInfo();
                startGameInfo.setMapName(input.readString());
                startGameInfo.setTeam1(kryo.readObject(input, List.class, teamSerializer));
                startGameInfo.setTeam2(kryo.readObject(input, List.class, teamSerializer));
                return startGameInfo;
            }
        });
    }
}
