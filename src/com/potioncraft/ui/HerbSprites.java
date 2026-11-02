package com.potioncraft.ui;

import com.potioncraft.model.potion.Ingredient;
import java.awt.image.BufferedImage;
import java.util.Locale;

/** Separate authored sprites, shared by the cabinet, recipes and stage briefing. */
public final class HerbSprites {
    public static final int WIDTH=32, HEIGHT=32;
    private static final BufferedImage[] CACHE=new BufferedImage[Ingredient.values().length];
    private HerbSprites(){}
    public static String assetPath(Ingredient herb){
        return "asset/herbs/"+herb.name().toLowerCase(Locale.ROOT)+".png";
    }
    public static synchronized BufferedImage get(Ingredient herb){
        BufferedImage image=CACHE[herb.ordinal()];
        if(image==null){
            image=AssetManager.read(assetPath(herb));
            if(image==null || image.getWidth()!=WIDTH || image.getHeight()!=HEIGHT)
                throw new IllegalStateException("Missing 32x32 ingredient artwork: "+assetPath(herb));
            CACHE[herb.ordinal()]=image;
        }
        return image;
    }
}
