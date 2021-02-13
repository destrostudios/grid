package com.destrostudios.grid.client;

import com.esotericsoftware.minlog.Log;

import java.io.IOException;

public class LocalhostMain2 {

    // intelliJ can't easily run 2 instances of the same main method at the same time
    // we can test multiple clients by using copies instead
    public static void main(String... args) throws IOException {
        Log.DEBUG();
        Main.startApplication("localhost", SimpleMain.getTestJwt(1));
    }
}
