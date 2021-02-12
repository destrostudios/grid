package com.destrostudios.grid.client;

import com.destrostudios.authtoken.JwtAuthentication;
import com.destrostudios.authtoken.NoValidateJwtService;
import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.actions.Action;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.gameproxy.ClientGameProxy;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.network.messages.Identify;
import com.destrostudios.grid.shared.MultipleOutputStream;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import com.esotericsoftware.kryonet.Client;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.out.println("First argument must be a jwt (usually passed by the destrostudios launcher).");
            return;
        }
        startGame(getClientProxy("destrostudios.com", args[0]));
    }

    static void startGame(GameProxy gameProxy) {
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
        new ClientApplication(gameProxy).start();
    }

    static GameProxy getClientProxy(String hostUrl, String jwt) throws IOException, InterruptedException {
        NetworkGridService gameService = new NetworkGridService(false);
        Client kryoClient = new Client(10_000_000, 10_000_000);
        GameClientModule<GridGame, Action> gameModule = new GameClientModule<>(gameService, kryoClient);
        ToolsClient client = new ToolsClient(kryoClient, gameModule);
        client.start(10_000, hostUrl, NetworkUtil.PORT);
        client.getKryoClient().sendTCP(new Identify(jwt));

        for (int i = 0; i < 10; i++) {
            if (!gameModule.getGames().isEmpty()) {
                break;
            }
            System.out.println("No games available, waiting...");
            Thread.sleep(500);
        }
        if (gameModule.getGames().isEmpty()) {
            throw new RuntimeException("No game found, is the server running?");
        }
        UUID gameId = gameModule.getGames().iterator().next().getId();
        JwtAuthentication authentication = new NoValidateJwtService().decode(jwt);
        return new ClientGameProxy(gameId, new PlayerInfo((int) authentication.user.id, authentication.user.login), gameModule);
    }
}
