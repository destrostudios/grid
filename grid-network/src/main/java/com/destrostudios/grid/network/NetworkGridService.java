package com.destrostudios.grid.network;

import com.destrostudios.gametools.network.shared.modules.game.GameService;
import com.destrostudios.gametools.network.shared.modules.game.NetworkRandom;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.random.MutableRandomProxy;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NetworkGridService implements GameService<GridGame, Action> {

    public NetworkGridService(boolean resolveActions) {
        this.resolveActions = resolveActions;
    }

    private boolean resolveActions;

    @Override
    public void initialize(Kryo kryo) {
        kryo.register(GridGame.class, new Serializer<GridGame>() {
            @Override
            public void write(Kryo kryo, Output output, GridGame object) {
                output.writeString(object.getState());
            }

            @Override
            public GridGame read(Kryo kryo, Input input, Class type) {
                GridGame gridGame = new GridGame(new MutableRandomProxy());
                gridGame.intializeGame(input.readString());
                return gridGame;
            }
        });
        kryo.register(PositionUpdateAction.class, new Serializer<PositionUpdateAction>() {
            @Override
            public void write(Kryo kryo, Output output, PositionUpdateAction object) {
                output.writeInt(object.getNewX());
                output.writeInt(object.getNewY());
                output.writeString(object.getPlayerIdentifier());
            }

            @Override
            public PositionUpdateAction read(Kryo kryo, Input input, Class<PositionUpdateAction> type) {
                return new PositionUpdateAction(input.readInt(), input.readInt(), input.readString());
            }
        });
        kryo.register(SkipRoundAction.class, new Serializer<SkipRoundAction>() {
            @Override
            public void write(Kryo kryo, Output output, SkipRoundAction object) {
                output.writeString(object.getPlayerIdentifier());
            }

            @Override
            public SkipRoundAction read(Kryo kryo, Input input, Class<SkipRoundAction> type) {
                return new SkipRoundAction(input.readString());
            }
        });
        kryo.register(CastSpellAction.class, new Serializer<CastSpellAction>() {
            @Override
            public void write(Kryo kryo, Output output, CastSpellAction object) {
                output.writeInt(object.getTargetX());
                output.writeInt(object.getTargetY());
                output.writeString(object.getPlayerIdentifier());
                output.writeInt(object.getSpell());
            }

            @Override
            public CastSpellAction read(Kryo kryo, Input input, Class<CastSpellAction> type) {
                return new CastSpellAction(input.readInt(), input.readInt(), input.readString(), input.readInt());
            }
        });
    }

    @Override
    public GridGame applyAction(GridGame state, Action action, NetworkRandom random) {
        if (!(state.getRandom() instanceof MutableRandomProxy)) {
            throw new UnsupportedOperationException("Networking requires a " + MutableRandomProxy.class.getSimpleName());
        }
        MutableRandomProxy stateRandom = (MutableRandomProxy) state.getRandom();
        stateRandom.setRandom(random::nextInt);
        state.registerAction(action);
        if (resolveActions) {
            while (state.triggeredHandlersInQueue()) {
                state.triggerNextHandler();
            }
        }
        return state;
    }

}
