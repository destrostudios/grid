package com.destrostudios.grid.client.replay;

import com.destrostudios.gametools.network.shared.modules.NetworkModule;
import com.destrostudios.gametools.network.shared.modules.game.messages.GameAction;
import com.destrostudios.gametools.network.shared.modules.game.messages.GameJoin;
import com.destrostudios.gametools.network.shared.modules.game.messages.ListGame;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.shared.StartGameInfo;
import com.esotericsoftware.kryonet.Connection;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameReplayModule extends NetworkModule {

    private final Map<UUID, GameReplay> replays = new ConcurrentHashMap<>();

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof ListGame) {
            ListGame<StartGameInfo> message = (ListGame<StartGameInfo>) object;
            onGameListed(message.gameId(), message.params());
        } else if (object instanceof GameJoin message) {
            onJoinGame(message.gameId(), (GridGame) message.state());
        } else if (object instanceof GameAction message) {
            onAction(message.gameId(), (Action) message.action(), message.randomHistory());
        }
    }

    private void onGameListed(UUID gameId, StartGameInfo params) {
        String gameState = replays.getOrDefault(gameId, new GameReplay(null, null)).getInitial();
        replays.put(gameId, new GameReplay(params, gameState));
    }

    private void onJoinGame(UUID gameId, GridGame gameState) {
        StartGameInfo gameInfo = replays.getOrDefault(gameId, new GameReplay(null, null)).getStartGameInfo();
        replays.put(gameId, new GameReplay(gameInfo, gameState.getState()));
    }

    private void onAction(UUID gameId, Action action, int[] randomHistory) {
        GameReplay replay = replays.get(gameId);
        if (replay.getInitial() == null && replay.getStartGameInfo() == null) {
            System.err.println("Failed to save replay, either state or startGameInfo is null.");
            return;
        }
        replay.append(new ActionReplay(action, randomHistory));
        try {
            ReplayIO.write(replay, Paths.get("replay.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
