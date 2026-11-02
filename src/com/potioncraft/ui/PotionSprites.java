package com.potioncraft.ui;

import com.potioncraft.model.potion.Potion;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/** Loads the separate authored potion icons used across the shop and recipe book. */
public final class PotionSprites {
    private static final Map<String,BufferedImage> CACHE=new HashMap<>();
    private PotionSprites() {}

    public static String assetPath(Potion potion) {
        return "asset/potions/"+potion.getId().toLowerCase(Locale.ROOT)+".png";
    }

    public static synchronized BufferedImage get(Potion potion) {
        if(potion==null || potion.isFailed()) return null;
        BufferedImage image=CACHE.get(potion.getId());
        if(image==null) {
            String path=assetPath(potion);
            image=AssetManager.read(path);
            if(image==null || image.getWidth()!=32 || image.getHeight()!=32)
                throw new IllegalStateException("Missing 32x32 potion artwork: "+path);
            CACHE.put(potion.getId(),image);
        }
        return image;
    }

    public static ImageIcon icon(Potion potion,int size) {
        BufferedImage source=get(potion);
        if(source==null) return null;
        BufferedImage scaled=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(source,0,0,size,size,null); g.dispose();
        return new ImageIcon(scaled);
    }
}
