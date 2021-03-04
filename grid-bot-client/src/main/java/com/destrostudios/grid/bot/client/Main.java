package com.destrostudios.grid.bot.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.bot.GridBotState;
import com.destrostudios.grid.bot.Team;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.network.KryoStartGameInfo;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.grid.util.GameOverUtils;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBot;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import com.destrostudios.turnbasedgametools.network.client.modules.game.ClientGameData;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameStartClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.jwt.JwtClientModule;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws IOException, InterruptedException {
        Log.DEBUG();
        Log.info(new Date().toString());// time reference for kryo logs
        String hostUrl = "localhost";// "destrostudios.com";
        String login = "Bot";
        ToolsClient client = getToolsClient(hostUrl, fakeJwt(-1, login));
        try {
            while (true) {
                GameClientModule<GridGame, Action> gameModule = client.getModule(GameClientModule.class);
                for (ClientGameData<GridGame, Action> game : gameModule.getJoinedGames()) {
                    while (game.applyNextAction(gameModule.getGameService())) {
                    }
                    if (GameOverUtils.getGameOverInfo(game.getState().getWorld()).isGameIsOver()) {
                        break;
                    }
                    boolean active = false;
                    List<Integer> activeCharacters = game.getState().getWorld().list(TurnComponent.class);
                    for (int activeCharacter : activeCharacters) {
                        NameComponent nameComponent = game.getState().getWorld().getComponent(activeCharacter, NameComponent.class);
                        if (nameComponent.getName().equals(login)) {
                            active = true;
                        }
                    }
                    if (active) {
                        LOG.info("calculating...");
                        GridBotState botState = new GridBotState(game.getState());
                        MctsBot<GridBotState, Action, Team, Map<Integer, List<Component>>> bot = com.destrostudios.grid.bot.Main.createBot(botState);
                        List<Action> actions = bot.sortedActions(botState.activeTeam());
                        gameModule.sendAction(game.getId(), actions.get(0));
                    }
                }

                Thread.sleep(1000);
            }
        } finally {
            client.stop();
        }
    }

    private static ToolsClient getToolsClient(String hostUrl, String jwt) throws IOException {
        NetworkGridService gameService = new NetworkGridService(true);
        Client kryoClient = new Client(10_000_000, 10_000_000);

        JwtClientModule jwtModule = new JwtClientModule(kryoClient);
        GameClientModule<GridGame, Action> gameModule = new GameClientModule<>(gameService, kryoClient);
        LobbyClientModule<StartGameInfo> lobbyModule = new LobbyClientModule<>(KryoStartGameInfo::initialize, kryoClient);
        GameStartClientModule<StartGameInfo> gameStartModule = new GameStartClientModule<>(KryoStartGameInfo::initialize, kryoClient);

        ToolsClient client = new ToolsClient(kryoClient, jwtModule, gameModule, lobbyModule, gameStartModule);
        client.start(10_000, hostUrl, NetworkUtil.PORT);
        jwtModule.login(jwt);
        lobbyModule.subscribeToGamesList();
        return client;
    }

    static String fakeJwt(int id, String login) {
        Map<String, ?> user = Map.of("id", id, "login", login);
        return JWT.create()
                .withIssuedAt(new Date())
                .withClaim("user", user)
                .sign(Algorithm.none());
    }
}
