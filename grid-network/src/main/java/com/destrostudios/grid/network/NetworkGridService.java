package com.destrostudios.grid.network;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.CastSpellAction;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.shared.modules.game.GameService;
import com.destrostudios.turnbasedgametools.network.shared.modules.game.NetworkRandom;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NetworkGridService implements GameService<GridGame, Action, StartGameInfo> {

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
                GridGame gridGame = new GridGame();
                gridGame.intializeGame(input.readString());
                return gridGame;
            }
        });
        kryo.register(PositionUpdateAction.class);
        kryo.register(SkipRoundAction.class);
        kryo.register(CastSpellAction.class);
        kryo.register(StartGameInfo.class);
    }

    @Override
    public GridGame startNewGame(StartGameInfo params) {
        GridGame gridGame = new GridGame();
        gridGame.initGame(params);
        return gridGame;
    }

    @Override
    public GridGame applyAction(GridGame state, Action action, NetworkRandom random) {
        state.registerAction(action);
        if (resolveActions) {
            while (state.triggeredHandlersInQueue()) {
                state.triggerNextHandler();
            }
        }
        return state;
    }

}
