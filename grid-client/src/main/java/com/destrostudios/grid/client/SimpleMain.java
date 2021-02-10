package com.destrostudios.grid.client;

import com.destrostudios.grid.GridGame;
import com.destrostudios.grid.client.gameproxy.SimpleGameProxy;
import com.destrostudios.grid.shared.StartGameInfo;

import java.io.IOException;

public class SimpleMain {

    public static void main(String... args) throws IOException, InterruptedException {
        GridGame gridGame = new GridGame();
        gridGame.initGame(StartGameInfo.getTestGameInfo());
        Main.startGame(new SimpleGameProxy(gridGame));
    }
}
