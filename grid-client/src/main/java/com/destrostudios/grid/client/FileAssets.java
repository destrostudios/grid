package com.destrostudios.grid.client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileAssets {

    public static String ROOT;

    public static void readRootFile() {
        try {
            ROOT = Files.readString(Paths.get("./assets.ini"));
        } catch (IOException ex) {
            System.err.println("Error while reading assets file: " + ex.getMessage());
        }
    }

    public static BufferedImage getImage(String filePath) {
        try {
            return ImageIO.read(new File(ROOT + filePath));
        } catch (IOException ex) {
            System.err.println("Error while reading image file '" + filePath + "': " + ex.getMessage());
        }
        return null;
    }
}
