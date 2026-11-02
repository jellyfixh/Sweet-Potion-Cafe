package com.potioncraft.test;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class FindCounterOffset {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("asset/background/counter_layer.png"));
        int firstNonTransparentY = -1;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 10) {
                    firstNonTransparentY = y;
                    break;
                }
            }
            if (firstNonTransparentY != -1) break;
        }
        System.out.println("First non-transparent Y in counter_layer: " + firstNonTransparentY + " out of " + img.getHeight() + " (" + ((double)firstNonTransparentY / img.getHeight() * 100) + "%)");
    }
}
