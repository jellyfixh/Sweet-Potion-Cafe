package com.potioncraft.ui;

import java.awt.*;

/** Integer-grid leather, paper and brass artwork shared by both shop journals. */
final class NotebookArt {
    static final Color INK=new Color(0x503b2d), FADED=new Color(0x80654c),
        GREEN=new Color(0x476747), RULE=new Color(0xcbb48b), PAPER=new Color(0xf3dfb6);
    private NotebookArt(){}
    private static void rect(Graphics2D g,int color,int x,int y,int w,int h){
        g.setColor(new Color(color));g.fillRect(x,y,w,h);
    }
    static void icon(Graphics2D g,int x,int y,boolean guests){
        if(guests){
            rect(g,0x664b35,x+6,y,10,3);rect(g,0xb58054,x+4,y+3,14,5);
            rect(g,0xdab183,x+7,y+8,8,6);rect(g,0x739276,x+3,y+15,16,7);
            rect(g,0xf7dfad,x+9,y+10,2,2);
        }else{
            rect(g,0x80573d,x+8,y,8,4);rect(g,0xe2d8b2,x+9,y+4,6,4);
            rect(g,0x644b36,x+5,y+8,14,15);rect(g,0x9dac75,x+7,y+10,10,11);
            rect(g,0xd9e0ac,x+8,y+10,3,7);rect(g,0xf4d296,x+7,y+21,10,2);
        }
    }
    static void closed(Graphics2D g,int x,int y,int w,int h,boolean guests,boolean hover){
        if(hover){rect(g,0x51382c,x-2,y-3,w+3,h+9);rect(g,0xa67b45,x+48,y+h-1,w-52,1);}
        Graphics2D q=(Graphics2D)g.create();q.translate(x+1,y-6);
        // A small, tilted grimoire rather than a rectangular toolbar tile.
        polygon(q,0x241b20,new int[]{5,13,39,44,38,9,2},new int[]{14,3,3,13,48,48,40});
        polygon(q,0xb58b4c,new int[]{12,37,41,34,7},new int[]{5,5,13,43,43});
        polygon(q,0xf2d394,new int[]{15,36,38,32,10},new int[]{7,7,13,39,39});
        for(int i=0;i<3;i++){
            q.setColor(new Color(0xb9945b));q.drawLine(13,33+i*3,33,33+i*3);
        }
        polygon(q,hover?0x9a6339:0x805034,new int[]{11,34,38,31,5},new int[]{3,3,9,40,40});
        polygon(q,0x593728,new int[]{5,11,14,8,2},new int[]{14,3,5,37,39});
        polygon(q,0xd3a557,new int[]{13,32,34,29,9},new int[]{7,7,11,35,35});
        polygon(q,0x68402e,new int[]{15,30,31,26,12},new int[]{10,10,13,32,32});
        // Gold crescent, tiny stars, corner guards, and raised spine clasps.
        rect(q,0xf9dc89,23,14,5,3);rect(q,0xe9b957,26,17,3,10);
        rect(q,0xf9dc89,21,27,6,3);rect(q,0xe9b957,23,25,5,3);
        rect(q,0xf9e3a3,17,19,2,7);rect(q,0xf9e3a3,15,21,6,2);
        rect(q,0xc89546,12,3,7,3);rect(q,0xffe5a2,12,3,4,2);
        rect(q,0xdab364,30,5,6,5);rect(q,0xffe5a2,31,5,3,2);
        rect(q,0xdab364,5,34,7,6);rect(q,0xffe5a2,6,34,4,2);
        rect(q,0xdab364,27,34,7,7);rect(q,0xffe5a2,28,35,4,2);
        for(int sy:new int[]{13,28}){rect(q,0x513323,2,sy,8,6);rect(q,0xd8aa56,1,sy,8,4);rect(q,0xffe7a2,2,sy,4,2);}
        polygon(q,guests?0x6f8765:0xa86945,new int[]{31,35,33,30,29},new int[]{39,39,48,45,47});
        rect(q,0xffe9b0,33,13,2,2);rect(q,0xffe9b0,14,30,2,2);
        q.dispose();
    }
    private static void polygon(Graphics2D g,int color,int[] xs,int[] ys){g.setColor(new Color(color));g.fillPolygon(xs,ys,xs.length);}
    private static void flourish(Graphics2D g,int x,int y,boolean mirror){
        Graphics2D q=(Graphics2D)g.create();q.translate(x,y);if(mirror)q.scale(-1,1);
        rect(q,0xbd9253,0,0,34,2);rect(q,0xbd9253,0,0,2,26);
        rect(q,0xd6b478,5,5,19,2);rect(q,0xd6b478,5,5,2,14);
        rect(q,0xbd9253,10,10,5,5);rect(q,0xe2c48b,18,10,3,3);
        rect(q,0xe2c48b,10,19,3,3);q.dispose();
    }
    static void pageCorner(Graphics2D g,boolean next,boolean enabled,boolean hover){
        Graphics2D q=(Graphics2D)g.create();q.translate(next?1088:112,716);if(!next)q.scale(-1,1);
        // Pixel steps describe the turned underside of a dog-eared page.
        for(int i=0;i<8;i++)rect(q,0xb49566,-8*(i+1),-64+i*8,8*(i+1),8);
        for(int i=0;i<7;i++)rect(q,hover && enabled?0xffedc0:0xe8ca8b,-8*(i+1),-58+i*8,8*(i+1)-2,8);
        q.setColor(new Color(0xf9e6b1));q.drawLine(-56,-3,-3,-56);
        int ink=enabled?0x79532e:0xbba478;
        rect(q,ink,-39,-24,22,4);rect(q,ink,-23,-30,4,4);rect(q,ink,-19,-26,4,4);
        rect(q,ink,-23,-18,4,4);rect(q,ink,-19,-22,4,4);
        q.dispose();
    }
    static void spread(Graphics2D g){
        // Stepped silhouette, thick brown leather cover, and loose notes behind it.
        rect(g,0x160e14,85,81,1036,684);rect(g,0x160e14,98,71,1010,704);
        rect(g,0xbda982,952,42,102,46);rect(g,0xf0dcac,956,38,94,44);
        rect(g,0xa88d66,968,49,64,2);rect(g,0xa88d66,968,57,48,2);
        rect(g,0x3c2723,80,78,1040,676);rect(g,0x3c2723,96,62,1008,708);
        rect(g,0x70482f,87,82,1026,665);rect(g,0x70482f,100,69,1000,694);
        rect(g,0x9a6941,94,87,1012,650);
        for(int x=110;x<1094;x+=14){rect(g,0xb58a57,x,74,5,2);rect(g,0xb58a57,x,752,5,2);}
        for(int y=96;y<740;y+=14){rect(g,0xb58a57,91,y,2,5);rect(g,0xb58a57,1107,y,2,5);}
        // Visible page block with irregular edge steps.
        rect(g,0xb69567,103,92,994,654);rect(g,0xd1b585,108,87,984,653);
        for(int y=728;y<741;y+=4)rect(g,0x96754e,117,y,969,1);
        int[] lx={112,128,535,568,600,600,568,535,128,112};
        int[] ly={91,81,81,88,105,729,715,710,720,713};
        int[] rx={600,632,665,1072,1088,1088,1072,665,632,600};
        int[] ry={105,88,81,81,91,713,720,710,715,729};
        g.setColor(PAPER);g.fillPolygon(lx,ly,10);g.fillPolygon(rx,ry,10);
        // Warm edge shading and fine gilt borders frame, but never cover, the text.
        for(int i=0;i<3;i++){
            rect(g,0xe5c99a,113+i*3,99,3,603);
            rect(g,0xe5c99a,1082-i*3,99,3,603);
        }
        flourish(g,123,91,false);flourish(g,1077,91,true);
        for(int x:new int[]{128,645}){
            rect(g,0xd6b478,x,184,420,1);
            rect(g,0xc39a5b,x+205,181,6,6);
        }
        // Paper grain is deterministic and unobtrusive behind the typesetting.
        for(int i=0;i<520;i++){
            int x=124+(i*137)%950,y=96+(i*71)%611;
            if(x>566 && x<636)continue;
            rect(g,i%3==0?0xe5cda1:0xedd7ae,x,y,2,1);
        }
        for(int i=0;i<7;i++){
            rect(g,0xe8cfa0,568+i*4,105+i*2,4,606-i*2);
            rect(g,0xd3b582,596-i*4,106+i*2,3,608-i*2);
            rect(g,0xe2c591,604+i*4,106-i*2,4,606+i*2);
        }
        rect(g,0x96734f,599,108,2,611);
        for(int y:new int[]{128,319,510,695}){
            rect(g,0xb6915b,590,y,20,3);rect(g,0xf0d7a5,592,y+3,16,2);
        }
        // A sage cloth ribbon peeks out beneath the spine.
        rect(g,0x344e40,619,720,18,61);rect(g,0x728267,622,720,12,54);
        g.setColor(new Color(0x344e40));g.fillPolygon(new int[]{619,637,628},new int[]{781,781,772},3);
        for(int x:new int[]{94,1086})for(int y:new int[]{83,731}){
            rect(g,0x5b3a29,x,y,20,17);rect(g,0xc19653,x+2,y+2,16,13);
            rect(g,0xf2cf84,x+4,y+3,12,3);rect(g,0x8b6036,x+7,y+8,4,4);
        }
    }
}
