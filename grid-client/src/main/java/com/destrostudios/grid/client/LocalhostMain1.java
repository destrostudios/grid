package com.destrostudios.grid.client;

import com.esotericsoftware.minlog.Log;

import java.io.IOException;

public class LocalhostMain1 {

    public static void main(String... args) throws IOException {
        Log.DEBUG();
        Main.startApplication("localhost", SimpleMain.getTestJwt(0));
    }
}
