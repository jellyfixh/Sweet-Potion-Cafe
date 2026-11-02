package com.potioncraft.test;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class InspectAssets {
    public static void main(String[] args) {
        File[] files = {
            new File("asset/background/background_no_counter.png"),
            new File("asset/background/counter_layer.png"),
            new File("asset/background/herb_box_cutout.png")
        };
        for (File f : files) {
            try {
                BufferedImage img = ImageIO.read(f);
                if (img != null) {
                    System.out.printf("File: %-30s Size: %dx%d%n", f.getName(), img.getWidth(), img.getHeight());
                } else {
                    System.out.println("Could not read: " + f.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
