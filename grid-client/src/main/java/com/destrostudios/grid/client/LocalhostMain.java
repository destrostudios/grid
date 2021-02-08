package com.destrostudios.grid.client;

import java.io.IOException;

public class LocalhostMain {

    public static void main(String... args) throws IOException, InterruptedException {
        Main.startGame(Main.getClientProxy("localhost"));
    }
}
