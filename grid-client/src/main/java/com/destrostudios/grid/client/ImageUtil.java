package com.destrostudios.grid.client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    public static BufferedImage getImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException ex) {
            System.err.println("Error while reading image file '" + filePath + "'.");
            ex.printStackTrace();
        }
        return null;
    }
}
