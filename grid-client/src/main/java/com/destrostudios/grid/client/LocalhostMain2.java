package com.destrostudios.grid.client;

import java.io.IOException;

public class LocalhostMain2 {

    // intelliJ can't run 2 instances of the same main method at the same time
    // we can test multiple clients by using copies instead
    public static void main(String... args) throws IOException, InterruptedException {
        Main.startGame(Main.getClientProxy("localhost"));
    }
}
