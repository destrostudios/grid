package com.destrostudios.grid.bot.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.bot.GridBotState;
import com.destrostudios.grid.bot.Team;
import com.destrostudios.grid.network.KryoStartGameInfo;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.bot.mcts.MctsBot;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import com.destrostudios.turnbasedgametools.network.client.modules.game.ClientGameData;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameStartClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.jwt.JwtClientModule;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Client;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String... args) throws IOException, InterruptedException {
        String hostUrl = "localhost";// "destrostudios.com";
        ToolsClient client = getToolsClient(hostUrl, fakeJwt(-1, "Bot"));
        try {
            while (true) {
                GameClientModule<GridGame, Action> gameModule = client.getModule(GameClientModule.class);
                for (ClientGameData<GridGame, Action> game : gameModule.getJoinedGames()) {
                    boolean updated = false;
                    while (game.applyNextAction(gameModule.getGameService())) {
                        updated = true;
                    }
                    if (updated) {
                        GridBotState botState = new GridBotState(game.getState());
                        MctsBot<GridBotState, Action, Team> bot = com.destrostudios.grid.bot.Main.createBot(botState);
                        List<Action> actions = bot.sortedActions(botState.activeTeam());
                        System.out.println(actions);
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
