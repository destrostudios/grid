package com.destrostudios.grid.client;

import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;

public class LocalhostMain1 {

    public static void main(String... args) throws IOException {
        Log.DEBUG();
        Log.info(new Date().toString());// time reference for kryo logs
        Main.startApplication("localhost", SimpleMain.getTestJwt(0));
    }
}
