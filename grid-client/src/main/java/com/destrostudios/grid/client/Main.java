package com.destrostudios.grid.client;

import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.network.KryoStartGameInfo;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.MultipleOutputStream;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameStartClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.jwt.JwtClientModule;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Client;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("First argument must be a jwt (usually passed by the destrostudios launcher).");
            return;
        }
        startApplication("destrostudios.com", args[0]);
    }

    static ClientApplication startApplication(String hostUrl, String jwt) throws IOException {
        return startApplication(getToolsClient(hostUrl, jwt), jwt);
    }

    static ClientApplication startApplication(ToolsClient toolsClient, String jwt) {
        try {
            FileOutputStream logFileOutputStream = new FileOutputStream("./log.txt");
            System.setOut(new PrintStream(new MultipleOutputStream(System.out, logFileOutputStream)));
            System.setErr(new PrintStream(new MultipleOutputStream(System.err, logFileOutputStream)));
        } catch (FileNotFoundException ex) {
            System.err.println("Error while accessing log file: " + ex.getMessage());
        }
        FileAssets.readRootFile();
        BlockAssets.registerBlocks();
        JMonkeyUtil.disableLogger();
        ClientApplication clientApplication = new ClientApplication(toolsClient, new NoValidateJwtService().decode(jwt).user);
        clientApplication.start();
        return clientApplication;
    }

    private static ToolsClient getToolsClient(String hostUrl, String jwt) throws IOException {
        NetworkGridService gameService = new NetworkGridService(false);
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
}
