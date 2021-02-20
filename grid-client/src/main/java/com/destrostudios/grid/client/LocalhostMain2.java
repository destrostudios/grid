package com.destrostudios.grid.client;

import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;

public class LocalhostMain2 {

    // intelliJ can't easily run 2 instances of the same main method at the same time
    // we can test multiple clients by using copies instead
    public static void main(String... args) throws IOException {
        Log.DEBUG();
        Log.info(new Date().toString());// time reference for kryo logs
        Main.startApplication("localhost", SimpleMain.getTestJwt(1));
    }
}
