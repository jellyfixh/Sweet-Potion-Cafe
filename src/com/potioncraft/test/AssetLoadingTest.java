package com.potioncraft.test;

import com.potioncraft.ui.AssetManager;
import com.potioncraft.ui.CustomerSprites;
import com.potioncraft.ui.HerbSprites;
import com.potioncraft.ui.CauldronSprites;
import com.potioncraft.model.potion.Ingredient;
import com.potioncraft.model.customer.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;

/** Also run from outside the project to verify standalone JAR resources. */
public final class AssetLoadingTest {
    private static void require(BufferedImage image,String name){
        if(image==null || image.getWidth()==0 || image.getHeight()==0)
            throw new AssertionError("Missing game artwork: "+name);
    }
    private static void samePixels(BufferedImage actual,Path expectedPath)throws Exception{
        BufferedImage expected=ImageIO.read(expectedPath.toFile());
        require(actual,expectedPath.toString());
        if(actual.getWidth()!=expected.getWidth() || actual.getHeight()!=expected.getHeight())
            throw new AssertionError("Loaded artwork from the wrong location: "+expectedPath);
        for(int y=0;y<expected.getHeight();y++)for(int x=0;x<expected.getWidth();x++)
            if(actual.getRGB(x,y)!=expected.getRGB(x,y))
                throw new AssertionError("Stale artwork replaced current pixels: "+expectedPath);
    }
    public static void main(String[] args)throws Exception{
        BufferedImage firstCauldron=CauldronSprites.frameAt(0);
        for(int i=0;i<CauldronSprites.FRAME_COUNT;i++){
            String path=CauldronSprites.assetPath(i);
            BufferedImage frame=CauldronSprites.frameAt((long)i*CauldronSprites.FRAME_MILLIS);
            require(frame,path);
            if(frame.getWidth()!=728 || frame.getHeight()!=688 || (frame.getRGB(0,0)>>>24)!=0)
                throw new AssertionError("Cauldron frames must retain their aligned transparent canvas");
            if(i>0 && java.util.Arrays.equals(firstCauldron.getRGB(0,0,728,688,null,0,728),frame.getRGB(0,0,728,688,null,0,728)))
                throw new AssertionError("Cauldron animation must change frames");
            if(args.length==1)samePixels(frame,Paths.get(args[0]).resolve(path));
        }
        if(CauldronSprites.frameAt(CauldronSprites.FRAME_MILLIS-1)!=firstCauldron
                || CauldronSprites.frameAt((long)CauldronSprites.FRAME_MILLIS*CauldronSprites.FRAME_COUNT)!=firstCauldron)
            throw new AssertionError("Cauldron animation must hold each frame, then loop");
        for(String location:new String[]{"parchment","forest","harbor","cave","palace"}){
            String path="asset/map/"+location+".png";
            BufferedImage landmark=AssetManager.read(path);
            require(landmark,path);
            if(!location.equals("parchment") && (!landmark.getColorModel().hasAlpha() || (landmark.getRGB(0,0)>>>24)>4))
                throw new AssertionError("Map landmark requires transparent margins: "+location);
            if(args.length==1)samePixels(landmark,Paths.get(args[0]).resolve(path));
        }
        require(AssetManager.getBackgroundNoCounter(),"room");
        require(AssetManager.getCounterLayer(),"counter");
        require(AssetManager.getHerbBox(),"herb cabinet");
        if(AssetManager.getCounterLayer().getHeight()!=218)
            throw new AssertionError("Counter must contain only the foreground strip");
        if(args.length==1){
            Path project=Paths.get(args[0]);
            samePixels(AssetManager.getBackgroundNoCounter(),project.resolve("asset/background/background_no_counter.png"));
            samePixels(AssetManager.getHerbBox(),project.resolve("asset/background/herb_box_cutout.png"));
            samePixels(AssetManager.read("asset/background/counter_layer.png"),project.resolve("asset/background/counter_layer.png"));
        }
        for(CustomerProfile profile:CustomerProfile.values())for(CustomerMood mood:CustomerMood.values()){
            BufferedImage sprite=CustomerSprites.get(profile,mood);
            require(sprite,profile+" "+mood);
            if(sprite.getWidth()!=CustomerSprites.WIDTH || sprite.getHeight()!=CustomerSprites.HEIGHT)
                throw new AssertionError("Customer sprite must be native 32x32");
            if(args.length==1)samePixels(sprite,Paths.get(args[0]).resolve(CustomerSprites.assetPath(profile,mood)));
        }
        for(Ingredient herb:Ingredient.values()){
            BufferedImage sprite=HerbSprites.get(herb);
            require(sprite,herb.name());
            boolean transparent=false,opaque=false;
            java.util.Set<Integer> colors=new java.util.HashSet<>();
            for(int y=0;y<sprite.getHeight();y++)for(int x=0;x<sprite.getWidth();x++){
                int pixel=sprite.getRGB(x,y),alpha=pixel>>>24;
                if(alpha!=0 && alpha!=255)throw new AssertionError("Herbs must have crisp alpha edges");
                transparent|=alpha==0;opaque|=alpha==255;
                if(alpha==255)colors.add(pixel);
            }
            if(!transparent || !opaque || colors.size()<4)throw new AssertionError("Missing transparent herb or lighting shades");
            if(args.length==1)samePixels(sprite,Paths.get(args[0]).resolve(HerbSprites.assetPath(herb)));
        }
        System.out.println("PASS: Scene, customer, herb and four looping cauldron frames load, including from the standalone JAR.");
    }
}
