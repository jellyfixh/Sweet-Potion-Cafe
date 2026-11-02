package com.potioncraft.test;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.customer.*;
import com.potioncraft.model.game.GameEngine;
import com.potioncraft.model.potion.RecipeBook;
import com.potioncraft.ui.CustomerSprites;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipFile;

/** Checks detailed replacement assets, separate exports and live waiting moods. */
public final class CustomerSpriteTest {
    private static int checks;
    private static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        SoundManager.setSoundEnabled(false);
        check(CustomerProfile.values().length==13,"Thirteen canonical identities");
        check(CustomerMood.fromPatience(1)==CustomerMood.NORMAL,"Fresh arrival is normal");
        check(CustomerMood.fromPatience(.50001)==CustomerMood.NORMAL,"Normal above half");
        check(CustomerMood.fromPatience(.5)==CustomerMood.IMPATIENT,"Half is impatient");
        check(CustomerMood.fromPatience(.25001)==CustomerMood.IMPATIENT,"Impatient above quarter");
        check(CustomerMood.fromPatience(.25)==CustomerMood.ANGRY,"Quarter is angry");
        check(CustomerMood.fromPatience(0)==CustomerMood.ANGRY,"Expired customer is angry");
        Set<Integer> heights=new HashSet<>(),identities=new HashSet<>();int[] tiers=new int[4];
        for(CustomerProfile p:CustomerProfile.values()){
            tiers[p.getRarity().ordinal()]++;
            Set<Integer> expressions=new HashSet<>();int minHeight=32,maxHeight=0;
            for(CustomerMood mood:CustomerMood.values()){
                BufferedImage actual=ImageIO.read(Paths.get(CustomerSprites.assetPath(p,mood)).toFile());
                check(actual!=null && actual.getWidth()==32 && actual.getHeight()==32,"Native 32x32 replacement: "+p+" "+mood);
                int[] pixels=actual.getRGB(0,0,32,32,null,0,32);
                check(expressions.add(Arrays.hashCode(pixels)),"Three different expression PNGs: "+p);
                Set<Integer> colors=new HashSet<>();int first=32,last=-1,coverage=0;
                for(int y=0;y<32;y++)for(int x=0;x<32;x++){
                    int argb=pixels[y*32+x];
                    if((argb>>>24)>=32){colors.add(argb&0xffffff);first=Math.min(first,y);last=Math.max(last,y);coverage++;}
                }
                check(colors.size()>=8 && colors.size()<=16,"Small shared pixel palette: "+p);
                check(coverage>70,"Full character artwork is present");
                check(first>0 && last==CustomerSprites.FOOT_Y-1,"Unclipped character shares the floor anchor");
                check((actual.getRGB(0,0)>>>24)==0,"Game background is truly transparent");
                minHeight=Math.min(minHeight,last-first+1);maxHeight=Math.max(maxHeight,last-first+1);
                if(mood==CustomerMood.NORMAL){
                    heights.add(last-first+1);check(identities.add(Arrays.hashCode(pixels)),"Distinct character identity");
                }
                Path white=Paths.get("output","customers","white",p.getAssetKey()+"-"+mood.getAssetKey()+".png");
                BufferedImage onWhite=ImageIO.read(white.toFile());
                check(onWhite.getWidth()==32 && onWhite.getHeight()==32,"Separate white PNG retains full frame resolution");
                check(onWhite.getRGB(0,0)==0xffffffff,"White-background export is white");
            }
            check(maxHeight-minHeight<=2,"Expression change preserves the character's size");
            Customer customer=CustomerFactory.create(p,RecipeBook.HEALTH_POTION);
            EnumSet<CustomerMood> seen=EnumSet.of(customer.getMood());CustomerMood previous=customer.getMood();
            for(int guard=0;!customer.isAngry() && guard<2000;guard++){
                customer.updatePatience(.05);CustomerMood now=customer.getMood();seen.add(now);
                check(now.ordinal()>=previous.ordinal(),"Waiting cannot make a face happier");previous=now;
            }
            check(seen.size()==3,"Actual patience visits all three expressions: "+p);
            check(customer.isAngry(),"Customer leaves after the angry-expression phase");
        }
        check(Arrays.equals(tiers,new int[]{4,4,3,2}),"Correct rarity counts");
        check(heights.size()>=6,"Different character heights remain visible");
        for(String name:new String[]{"customers-white-39.zip","customers-transparent-39.zip"}){
            try(ZipFile zip=new ZipFile(Paths.get("output","customers",name).toFile())){
                check(zip.size()==39,"ZIP contains exactly 39 separate images");
                check(zip.stream().allMatch(e->e.getName().endsWith(".png")),"ZIP contains only PNGs");
            }
        }
        Path temp=Files.createTempDirectory(Paths.get("build"),"customer-sprite-test-");
        GameEngine game=new GameEngine(temp.resolve("save.properties"),new Random(4));
        game.loadLevel(0);game.startManual();game.advance(26);
        Set<CustomerProfile> present=new HashSet<>();
        for(Customer c:game.getActiveCustomers())check(present.add(c.getProfile()),"No duplicate customer waiting in shop");
        check(present.size()==4,"Four different Common characters appear together");
        Customer watched=game.getActiveCustomers().get(0);CustomerMood mood=watched.getMood();double ratio=watched.getPatiencePercentage();
        game.pause();game.advance(15);
        check(watched.getMood()==mood && watched.getPatiencePercentage()==ratio,"Pause freezes patience and expression");
        game.stop();
        System.out.println("PASS: "+checks+" customer checks; 39 native 32x32 replacement PNGs, shared anchors, varied heights, timed moods and ZIPs.");
    }
}
