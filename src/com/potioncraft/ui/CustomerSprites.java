package com.potioncraft.ui;

import com.potioncraft.model.customer.*;
import java.awt.image.BufferedImage;

/** Native 32x32 sprites prepared from the approved pixel-art character strips. */
public final class CustomerSprites {
    public static final int WIDTH=32, HEIGHT=32, FOOT_Y=31;
    private static final BufferedImage[][] CACHE=new BufferedImage[CustomerProfile.values().length][CustomerMood.values().length];
    private CustomerSprites(){}
    public static String assetPath(CustomerProfile p,CustomerMood mood){
        return "asset/customers/"+p.getAssetKey()+"/"+mood.getAssetKey()+".png";
    }
    public static synchronized BufferedImage get(CustomerProfile p,CustomerMood mood){
        if(p==null)p=CustomerProfile.ELDER_HERBALIST;
        if(mood==null)mood=CustomerMood.NORMAL;
        BufferedImage image=CACHE[p.ordinal()][mood.ordinal()];
        if(image==null){
            String path=assetPath(p,mood);image=AssetManager.read(path);
            if(image==null || image.getWidth()!=WIDTH || image.getHeight()!=HEIGHT)
                throw new IllegalStateException("Missing 32x32 customer artwork: "+path);
            CACHE[p.ordinal()][mood.ordinal()]=image;
        }
        return image;
    }
}
