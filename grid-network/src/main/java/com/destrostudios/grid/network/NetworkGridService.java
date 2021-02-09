package com.destrostudios.grid.network;

import com.destrostudios.grid.components.*;
import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.update.eventbus.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.shared.GameService;
import com.destrostudios.turnbasedgametools.network.shared.NetworkRandom;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NetworkGridService implements GameService<Game, ComponentUpdateEvent<?>> {


    @Override
    public void initialize(Kryo kryo) {
        kryo.register(Game.class, new Serializer<Game>() {
            @Override
            public void write(Kryo kryo, Output output, Game object) {
                output.writeString(object.getState());
            }

            @Override
            public Game read(Kryo kryo, Input input, Class type) {
                Game game = new Game();
                game.intializeGame(input.readString());
                return game;
            }
        });
        kryo.register(Component[].class);
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
        kryo.register(MovementPointsComponent.class, new Serializer<MovementPointsComponent>() {
            @Override
            public void write(Kryo kryo, Output output, MovementPointsComponent object) {
                output.writeInt(object.getMovementPoints());
            }

            @Override
            public MovementPointsComponent read(Kryo kryo, Input input, Class type) {
                return new MovementPointsComponent(input.readInt());
            }
        });
        kryo.register(HealthPointsComponent.class, new Serializer<HealthPointsComponent>() {
            @Override
            public void write(Kryo kryo, Output output, HealthPointsComponent object) {
                output.writeInt(object.getHealth());
            }

            @Override
            public HealthPointsComponent read(Kryo kryo, Input input, Class type) {
                return new HealthPointsComponent(input.readInt());
            }
        });
        kryo.register(MaxHealthComponent.class, new Serializer<MaxHealthComponent>() {
            @Override
            public void write(Kryo kryo, Output output, MaxHealthComponent object) {
                output.writeInt(object.getMaxHealth());
            }

            @Override
            public MaxHealthComponent read(Kryo kryo, Input input, Class type) {
                return new MaxHealthComponent(input.readInt());
            }
        });
        kryo.register(AttackPointsComponent.class, new Serializer<AttackPointsComponent>() {
            @Override
            public void write(Kryo kryo, Output output, AttackPointsComponent object) {
                output.writeInt(object.getAttackPoints());
            }

            @Override
            public AttackPointsComponent read(Kryo kryo, Input input, Class type) {
                return new AttackPointsComponent(input.readInt());
            }
        });


        kryo.register(RoundComponent.class);
        kryo.register(TeamComponent.class, new Serializer<TeamComponent>() {
            @Override
            public void write(Kryo kryo, Output output, TeamComponent o) {
                output.writeInt(o.getTeam());
            }

            @Override
            public TeamComponent read(Kryo kryo, Input input, Class aClass) {
                return new TeamComponent(input.readInt());
            }
        });
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
        game.initGame(StartGameInfo.getTestGameInfo());
        return game;
    }

    @Override
    public Game applyAction(Game state, ComponentUpdateEvent<? extends Component> action, NetworkRandom random) {
        state.update(action);
        return state;
    }

}
