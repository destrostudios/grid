package com.destrostudios.grid.client;

import com.destrostudios.grid.client.gameproxy.SimpleGameProxy;
import com.destrostudios.grid.game.Game;
import java.io.IOException;

public class SimpleMain {

    public static void main(String... args) throws IOException, InterruptedException {
        Game game = new Game();
        game.initGame();
        Main.startGame(new SimpleGameProxy(game));
    }
}
