package com.destrostudios.grid.network;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.shared.GameService;
import com.destrostudios.turnbasedgametools.network.shared.NetworkRandom;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkGridService implements GameService<Game, ComponentUpdateEvent<?>> {


    @Override
    public void initialize(Kryo kryo) {
//        kryo.register(Game.class, new Serializer<Game>() {
//            @Override
//            public void write(Kryo kryo, Output output, Game object) {
//                output.writeString(object.getState());
//            }
//
//            @Override
//            public Game read(Kryo kryo, Input input, Class type) {
//                Game game = new Game();
//                game.intializeGame(input.readString());
//                return game;
//            }
//        });
        kryo.register(Component[].class);
        kryo.register(Game.class, new Serializer<Game>() {
            @Override
            public void write(Kryo kryo, Output output, Game object) {
                Set<Map.Entry<Integer, List<Component>>> entries = object.getWorld().getWorld().entrySet();
                output.writeInt(entries.size());
                for (Map.Entry<Integer, List<Component>> entry : entries) {
                    output.writeInt(entry.getKey());
                    kryo.writeObject(output, entry.getValue().toArray(Component[]::new));
                }
            }

            @Override
            public Game read(Kryo kryo, Input input, Class type) {
                Game game = new Game();
                int size = input.readInt();
                for (int i = 0; i < size; i++) {
                    int entity = input.readInt();
                    Component[] components = kryo.readObject(input, Component[].class);
                    for (Component component : components) {
                        game.getWorld().addComponent(entity, component);
                    }
                }
                return game;
            }
        });
        kryo.register(ComponentUpdateEvent.class, new Serializer<ComponentUpdateEvent<?>>() {
            @Override
            public void write(Kryo kryo, Output output, ComponentUpdateEvent<?> object) {
                output.writeInt(object.getEntity());
                kryo.writeClassAndObject(output, object.getComponent());
            }

            @Override
            public ComponentUpdateEvent<?> read(Kryo kryo, Input input, Class type) {
                return new ComponentUpdateEvent<>(input.readInt(), (Component) kryo.readClassAndObject(input));
            }
        });
        kryo.register(PlayerComponent.class, new Serializer<PlayerComponent>() {
            @Override
            public void write(Kryo kryo, Output output, PlayerComponent object) {
                output.writeString(object.getName());
            }

            @Override
            public PlayerComponent read(Kryo kryo, Input input, Class type) {
                return new PlayerComponent(input.readString());
            }
        });
        kryo.register(MovingComponent.class);
        kryo.register(PositionComponent.class, new Serializer<PositionComponent>() {
            @Override
            public void write(Kryo kryo, Output output, PositionComponent object) {
                output.writeInt(object.getX());
                output.writeInt(object.getY());
            }

            @Override
            public PositionComponent read(Kryo kryo, Input input, Class type) {
                return new PositionComponent(input.readInt(), input.readInt());
            }
        });
    }

    @Override
    public Game startNewGame() {
        Game game = new Game();
        game.fillTestGameData();
        return game;
    }

    @Override
    public Game applyAction(Game state, ComponentUpdateEvent<?> action, NetworkRandom random) {
        state.update(action);
        return state;
    }

}
