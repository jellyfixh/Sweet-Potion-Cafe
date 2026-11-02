package com.potioncraft.tools;

import com.potioncraft.model.customer.*;
import com.potioncraft.ui.CustomerSprites;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/** Native 32x32 strip slicing, shared anchors and one small palette per character. */
public final class PrepareCustomerArtwork {
    private static Rectangle bounds(BufferedImage image,int left,int right){
        int minX=right,minY=image.getHeight(),maxX=-1,maxY=-1;
        for(int y=0;y<image.getHeight();y++)for(int x=left;x<right;x++){
            if((image.getRGB(x,y)>>>24)>=32){
                minX=Math.min(minX,x);maxX=Math.max(maxX,x);
                minY=Math.min(minY,y);maxY=Math.max(maxY,y);
            }
        }
        if(maxX<0)throw new IllegalArgumentException("Empty expression slot");
        return new Rectangle(minX,minY,maxX-minX+1,maxY-minY+1);
    }
    public static void main(String[] args)throws Exception{
        CustomerProfile[] selected=args.length==0?CustomerProfile.values():new CustomerProfile[]{CustomerProfile.valueOf(args[0].toUpperCase(Locale.ROOT))};
        // Validate the complete source set before replacing any live customer.
        BufferedImage[] strips=new BufferedImage[CustomerProfile.values().length];
        for(CustomerProfile p:selected){
            Path raw=Paths.get("output","imagegen","customer-sources","native32",p.getAssetKey()+".png");
            if(!Files.isRegularFile(raw))throw new IllegalStateException("Missing character strip: "+raw);
            strips[p.ordinal()]=ImageIO.read(raw.toFile());
            if(strips[p.ordinal()]==null)throw new IllegalStateException("Invalid strip: "+raw);
        }
        for(CustomerProfile p:selected){
            BufferedImage strip=strips[p.ordinal()];Rectangle[] frames=new Rectangle[3];int maxW=0,maxH=0;
            for(int i=0;i<3;i++){
                frames[i]=bounds(strip,strip.getWidth()*i/3,strip.getWidth()*(i+1)/3);
                maxW=Math.max(maxW,frames[i].width);maxH=Math.max(maxH,frames[i].height);
            }
            double targetHeight=Math.min(30,p.getSpriteHeight());
            double scale=Math.min(targetHeight/maxH,30.0/maxW);
            BufferedImage[] prepared=new BufferedImage[3];
            for(CustomerMood mood:CustomerMood.values()){
                Rectangle crop=frames[mood.ordinal()];
                int w=(int)Math.round(crop.width*scale),h=(int)Math.round(crop.height*scale);
                int x=(CustomerSprites.WIDTH-w)/2,y=CustomerSprites.FOOT_Y-h;
                BufferedImage output=new BufferedImage(CustomerSprites.WIDTH,CustomerSprites.HEIGHT,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g=output.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(strip,x,y,x+w,y+h,crop.x,crop.y,crop.x+crop.width,crop.y+crop.height,null);g.dispose();
                prepared[mood.ordinal()]=output;
            }
            reducePalette(prepared,16);
            for(CustomerMood mood:CustomerMood.values()){
                Path path=Paths.get(CustomerSprites.assetPath(p,mood));Files.createDirectories(path.getParent());
                ImageIO.write(prepared[mood.ordinal()],"png",path.toFile());
            }
        }
        System.out.println("Prepared "+(selected.length*3)+" native 32x32 sprites with 16-color shared palettes and a common floor anchor.");
    }
    private static int channel(int color,int index){return (color>>((2-index)*8))&255;}
    /** Weighted median-cut palette shared by all moods prevents color flicker. */
    private static void reducePalette(BufferedImage[] frames,int limit){
        Map<Integer,Integer> counts=new HashMap<>();
        for(BufferedImage frame:frames)for(int y=0;y<32;y++)for(int x=0;x<32;x++){
            int color=frame.getRGB(x,y);if((color>>>24)>=128)counts.merge(color&0xffffff,1,Integer::sum);
        }
        List<List<Integer>> groups=new ArrayList<>();groups.add(new ArrayList<>(counts.keySet()));
        while(groups.size()<limit){
            int chosen=-1,axis=0;double best=-1;
            for(int i=0;i<groups.size();i++){
                List<Integer> group=groups.get(i);if(group.size()<2)continue;
                int population=0;for(int color:group)population+=counts.get(color);
                for(int c=0;c<3;c++){
                    int min=255,max=0;for(int color:group){int value=channel(color,c);min=Math.min(min,value);max=Math.max(max,value);}
                    double score=(max-min)*Math.sqrt(population);
                    if(score>best){best=score;chosen=i;axis=c;}
                }
            }
            if(chosen<0)break;
            List<Integer> group=groups.remove(chosen);final int sortAxis=axis;
            group.sort(Comparator.comparingInt(c->channel(c,sortAxis)));
            int total=0;for(int color:group)total+=counts.get(color);
            int sum=0,split=1;
            for(int i=0;i<group.size()-1;i++){sum+=counts.get(group.get(i));split=i+1;if(sum>=total/2)break;}
            groups.add(new ArrayList<>(group.subList(0,split)));groups.add(new ArrayList<>(group.subList(split,group.size())));
        }
        List<Integer> palette=new ArrayList<>();
        for(List<Integer> group:groups){
            long r=0,g=0,b=0,n=0;
            for(int color:group){int count=counts.get(color);r+=channel(color,0)*count;g+=channel(color,1)*count;b+=channel(color,2)*count;n+=count;}
            palette.add(((int)(r/n)<<16)|((int)(g/n)<<8)|(int)(b/n));
        }
        Map<Integer,Integer> mapped=new HashMap<>();
        for(BufferedImage frame:frames)for(int y=0;y<32;y++)for(int x=0;x<32;x++){
            int argb=frame.getRGB(x,y);if((argb>>>24)<128){frame.setRGB(x,y,0);continue;}
            int color=argb&0xffffff;
            int result=mapped.computeIfAbsent(color,key->{
                int nearest=palette.get(0),distance=Integer.MAX_VALUE;
                for(int candidate:palette){
                    int dr=channel(key,0)-channel(candidate,0),dg=channel(key,1)-channel(candidate,1),db=channel(key,2)-channel(candidate,2);
                    int d=dr*dr+dg*dg+db*db;if(d<distance){distance=d;nearest=candidate;}
                }
                return 0xff000000|nearest;
            });
            frame.setRGB(x,y,result);
        }
    }
}
