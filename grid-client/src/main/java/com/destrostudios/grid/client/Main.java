package com.destrostudios.grid.client;

import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.game.Game;
import com.destrostudios.grid.network.NetworkGridService;
import com.destrostudios.grid.shared.MultipleOutputStream;
import com.destrostudios.grid.update.ComponentUpdateEvent;
import com.destrostudios.turnbasedgametools.network.client.GamesClient;
import com.destrostudios.turnbasedgametools.network.shared.NetworkUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        startGame("destrostudios.com");
    }

    static void startGame(String hostUrl) throws IOException, InterruptedException {
        NetworkGridService gameService = new NetworkGridService();
        GamesClient<Game, ComponentUpdateEvent<?>> client = new GamesClient<>(hostUrl, NetworkUtil.PORT, 10_000, gameService);

        for (int i = 0; i < 10; i++) {
            if (!client.getGames().isEmpty()) {
                System.out.println(client.getGames().iterator().next().getState().getWorld().getWorld());
                break;
            }
            System.out.println("No games available, waiting...");
            Thread.sleep(500);
        }
        if (client.getGames().isEmpty()) {
            throw new RuntimeException("No game found, is the server running?");
        }
        UUID gameId = client.getGames().iterator().next().getId();

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
        new ClientApplication(new GameProxy(gameId, client)).start();
    }
}
