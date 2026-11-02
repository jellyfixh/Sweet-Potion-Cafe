package com.potioncraft.ui;

import com.potioncraft.model.customer.*;
import com.potioncraft.model.potion.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/** Original integer-grid sprites and environments, painted with Java2D only. */
public final class PixelArt {
    private static final BufferedImage[] SHOPS=new BufferedImage[4];
    private PixelArt(){}
    private static void r(Graphics2D g,int color,int x,int y,int w,int h){g.setColor(new Color(color));g.fillRect(x,y,w,h);}
    public static BufferedImage shop(int world){
        if(SHOPS[world-1]!=null)return SHOPS[world-1];
        BufferedImage b=new BufferedImage(600,150,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=b.createGraphics();
        int[] wall={0x33483e,0x2b4055,0x3c3351,0x4e3338};
        int[] sky={0x8fae88,0x688fab,0x8a78a7,0xc38b64};
        r(g,wall[world-1],0,0,600,150);
        for(int y=0;y<120;y+=16)for(int x=-(y/16%2)*24;x<600;x+=48){
            r(g,0x263235,x,y,47,1);r(g,0x263235,x,y,1,16);
            r(g,wall[world-1]+0x050505,x+3,y+3,40,1);
        }
        // Three tall windows look out into each chapter's landscape.
        for(int x:new int[]{90,267,444}){
            r(g,0x1a242c,x-5,10,72,89);r(g,0x9b7752,x-2,12,66,85);
            r(g,sky[world-1],x+2,16,58,76);
            r(g,0xe6d2a0,x+37,22,11,11);r(g,sky[world-1],x+33,20,9,10);
            for(int k=0;k<6;k++){
                int tx=x+3+k*10,ty=55+(k%3)*6;
                if(world==1){r(g,0x3c675b,tx,ty,8,39);r(g,0x5e8265,tx-3,ty+8,14,11);}
                else if(world==2){r(g,0x456b87,x+2,65,58,27);r(g,0x89b1bd,tx,74+k%2*8,7,1);}
                else {r(g,world==3?0x594b76:0x81575b,tx,ty,8,40);r(g,0xbca589,tx+2,ty+6,2,3);}
            }
            r(g,0x644d3e,x+28,15,3,78);r(g,0x644d3e,x,53,62,3);
            r(g,0xc4a26a,x-7,93,76,5);
        }
        // Side shelving, bottles, hanging herbs, lanterns and an alchemist's sign.
        Random random=new Random(77);
        for(int sx:new int[]{5,526})for(int sy:new int[]{34,70,106}){
            r(g,0x1c2930,sx,sy-24,69,24);r(g,0xa58055,sx-2,sy,73,4);
            for(int i=0;i<5;i++)bottle(g,sx+5+i*12,sy-20,1,Color.getHSBColor(random.nextFloat(),.4f,.85f));
        }
        for(int x:new int[]{179,394}){
            r(g,0x9a7654,x,0,2,18);r(g,0xd3a55f,x-5,18,12,18);
            r(g,0xf5d898,x-3,21,8,11);r(g,0x71513b,x-6,17,14,3);
        }
        for(int x=215;x<385;x+=23){
            r(g,0xa88a5b,x,0,1,13);r(g,0x587d61,x-3,12,7,8);r(g,0x6e9970,x-5,17,10,7);
        }
        r(g,0x1e272c,0,116,600,34);r(g,0xb78a57,0,118,600,6);r(g,0xe0b877,0,118,600,2);
        r(g,0x77543e,0,124,600,26);
        for(int x=0;x<600;x+=50){r(g,0x463c34,x,125,2,25);r(g,0x956a47,x+5,133,40,1);}
        r(g,0x352f2b,0,146,600,4);
        g.dispose();SHOPS[world-1]=b;return b;
    }
    public static void bottle(Graphics2D g,int x,int y,int scale,Color liquid){
        Graphics2D q=(Graphics2D)g.create();q.translate(x,y);q.scale(scale,scale);
        r(q,0x21313e,3,0,6,4);r(q,0xba9267,4,0,4,2);
        r(q,0xbed3cf,3,4,6,4);r(q,0xbed3cf,1,8,10,10);r(q,0xbed3cf,3,18,6,1);
        q.setColor(liquid);q.fillRect(2,10,8,7);q.fillRect(4,8,4,3);
        r(q,0xecf2d9,3,9,2,5);r(q,0xecf2d9,4,5,1,2);q.dispose();
    }
    public static void herb(Graphics2D g,Ingredient herb,int x,int y,int s){
        Graphics2D q=(Graphics2D)g.create();
        q.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        q.drawImage(HerbSprites.get(herb),x,y,HerbSprites.WIDTH*s,HerbSprites.HEIGHT*s,null);
        q.dispose();
    }
    public static void customer(Graphics2D g,CustomerProfile profile,int x,int y,int scale){
        customer(g,profile,CustomerMood.NORMAL,x,y,scale);
    }
    public static void customer(Graphics2D g,Customer customer,int x,int y,int scale){
        g.drawImage(CustomerSprites.get(customer.getProfile(),customer.getMood()),x,y,48*scale,48*scale,null);
    }
    public static void customer(Graphics2D g,CustomerProfile profile,CustomerMood mood,int x,int y,int scale){
        g.drawImage(CustomerSprites.get(profile,mood),x,y,32*scale,32*scale,null);
    }
    public static void cauldron(Graphics2D g,int x,int y,int s,double time,boolean active){
        Graphics2D q=(Graphics2D)g.create();q.translate(x,y);q.scale(s,s);
        r(q,0x6c4938,4,39,48,4);r(q,0xaf7049,8,36,40,3);
        for(int i=0;i<5;i++){r(q,0xe4a459,12+i*7,32-(i%2)*2,5,8);r(q,0xf5d783,14+i*7,34,2,4);}
        r(q,0x111e28,4,9,49,8);r(q,0x314752,7,16,43,15);r(q,0x314752,12,30,34,5);
        r(q,0x536d76,10,17,5,11);r(q,0x82969a,11,18,2,6);
        r(q,0x1b2e38,43,17,6,13);r(q,0x1b2e38,16,32,26,3);
        r(q,0xb29c6d,26,21,7,7);r(q,0xe0c48b,28,23,3,3);
        r(q,0x8eae9f,7,9,43,2);r(q,active?0x98dab6:0x729884,10,11,37,3);
        for(int i=0;i<4;i++){
            int by=(int)((time*12+i*7)%22);
            r(q,active?0xb2e2c2:0x7d9a8d,17+i*7,8-by,2+i%2,2+i%2);
        }
        q.dispose();
    }
}
