package com.potioncraft.test;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class InspectHerbBox {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("asset/background/herb_box_cutout.png"));
        System.out.println("Herb box size: " + img.getWidth() + "x" + img.getHeight());
        // Top shelf row: Y ~ 20 to 80
        // Middle shelf row: Y ~ 85 to 145
        // Bottom drawers: Y ~ 150 to 200
        // Columns: X ~ 15 to 95, 100 to 180, 185 to 265
    }
}
