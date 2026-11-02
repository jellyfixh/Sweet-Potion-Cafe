package com.potioncraft.ui;

import java.awt.image.BufferedImage;

/** Original aligned artwork, looping independently of the brewing/game clock. */
public final class CauldronSprites {
    public static final int FRAME_COUNT=4;
    public static final int FRAME_MILLIS=180;
    private static final BufferedImage[] FRAMES=loadFrames();

    private CauldronSprites() {}

    public static String assetPath(int index){
        return "asset/cauldron/cauldron_frame"+(index+1)+".png";
    }

    private static BufferedImage[] loadFrames(){
        BufferedImage[] frames=new BufferedImage[FRAME_COUNT];
        for(int i=0;i<frames.length;i++){
            frames[i]=AssetManager.read(assetPath(i));
            if(frames[i]==null)throw new IllegalStateException("Missing cauldron artwork: "+assetPath(i));
        }
        return frames;
    }

    public static BufferedImage frameAt(long elapsedMillis){
        return FRAMES[(int)Math.floorMod(Math.floorDiv(elapsedMillis,FRAME_MILLIS),FRAME_COUNT)];
    }
}
