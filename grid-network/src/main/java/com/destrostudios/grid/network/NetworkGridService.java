package com.destrostudios.grid.network;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.network.messages.Identify;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.actions.PositionUpdateAction;
import com.destrostudios.grid.actions.SkipRoundAction;
import com.destrostudios.turnbasedgametools.network.shared.GameService;
import com.destrostudios.turnbasedgametools.network.shared.NetworkRandom;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NetworkGridService implements GameService<GridGame, Action> {


    @Override
    public void initialize(Kryo kryo) {
        kryo.register(Identify.class);

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
    }

    @Override
    public GridGame startNewGame() {
        GridGame gridGame = new GridGame();
        gridGame.initGame(StartGameInfo.getTestGameInfo());
        return gridGame;
    }

    @Override
    public GridGame applyAction(GridGame state, Action action, NetworkRandom random) {
        state.registerAction(action);
        return state;
    }

}
