package com.destrostudios.grid.client;

import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.shared.MultipleOutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) {
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
        new ClientApplication().start();
    }
}
