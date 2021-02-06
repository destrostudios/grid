package com.destrostudios.grid.client;

import java.io.IOException;

public class Main2 {

    // intelliJ can't run 2 instances of Main at the same time
    // we can test multiple clients by using Main & Main2 instead
    public static void main(String... args) throws IOException, InterruptedException {
        Main.main(args);
    }
}
