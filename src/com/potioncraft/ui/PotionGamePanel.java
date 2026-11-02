package com.potioncraft.ui;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.customer.*;
import com.potioncraft.model.game.*;
import com.potioncraft.model.potion.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/** Scalable Java2D interface. All game rules remain in the model package. */
public final class PotionGamePanel extends JPanel {
    public static final int WIDTH=1200,HEIGHT=820;
    private static final Color INK=new Color(0x2c1c25),PAPER=new Color(0xffedca),MUTED=new Color(0xc5b399),
        GOLD=new Color(0xf0bf72),TEAL=new Color(0xa9c68b),RED=new Color(0xee9c8f),PANEL=new Color(0x432c32);
    private static final int SCENE_Y=90, SCENE_H=686, COUNTER_Y=581;
    private static final Color[] WORLD_COLORS={new Color(0x7fae88),new Color(0x85b4ce),new Color(0xb49cd0),new Color(0xdfaa73)};
    private final GameEngine game;
    private final List<HitArea> areas=new ArrayList<>();
    private final javax.swing.Timer repaintTimer;
    private Screen screen=Screen.TITLE;
    private BufferedImage titleBackground;
    private Overlay overlay=Overlay.NONE;
    private Customer selected;
    private int hoverX=-1,hoverY=-1,bookPage,guestPage,selectedWorld;
    private boolean overlayResumes;
    private double scale=1,offsetX,offsetY;
    private String hint="";
    private enum Screen{TITLE,MAP,BRIEF,PLAY,RESULT}
    private enum Overlay{NONE,BOOK,GUESTS,HELP,PAUSE,STAGES}
    private static final class HitArea {
        final Rectangle bounds;final Runnable action;
        HitArea(int x,int y,int w,int h,Runnable a){bounds=new Rectangle(x,y,w,h);action=a;}
    }
    public PotionGamePanel(GameEngine game){
        this.game=game;setPreferredSize(new Dimension(WIDTH,HEIGHT));setBackground(INK);setFocusable(true);
        addMouseListener(new MouseAdapter(){@Override public void mousePressed(MouseEvent e){
            requestFocusInWindow();int x=(int)((e.getX()-offsetX)/scale),y=(int)((e.getY()-offsetY)/scale);
            for(int i=areas.size()-1;i>=0;i--){HitArea a=areas.get(i);if(a.bounds.contains(x,y)){a.action.run();repaint();break;}}
        }});
        addMouseMotionListener(new MouseMotionAdapter(){@Override public void mouseMoved(MouseEvent e){
            hoverX=(int)((e.getX()-offsetX)/scale);hoverY=(int)((e.getY()-offsetY)/scale);
            boolean hit=false;for(HitArea a:areas)if(a.bounds.contains(hoverX,hoverY)){hit=true;break;}
            setCursor(Cursor.getPredefinedCursor(hit?Cursor.HAND_CURSOR:Cursor.DEFAULT_CURSOR));
        }});
        bind("released SPACE",()->{if(screen==Screen.PLAY && overlay==Overlay.NONE){if(game.getSkillCheck()!=null)game.skillHit();else game.beginBrew(selected);}});
        bind("ESCAPE",()->{if(overlay!=Overlay.NONE)closeOverlay();else if(screen==Screen.PLAY)openOverlay(Overlay.PAUSE);});
        bind("B",()->{if(screen==Screen.TITLE)return;if(overlay==Overlay.NONE)openBook();else if(overlay==Overlay.BOOK)closeOverlay();});
        bind("R",()->{if(screen==Screen.PLAY && overlay==Overlay.NONE){game.trashHeldPotion();game.clearCauldron();}});
        bind("ENTER",()->{if(overlay!=Overlay.NONE)return;if(screen==Screen.TITLE)toMap();else if(screen==Screen.BRIEF)startStage(true);else if(screen==Screen.PLAY)serveSelected();});
        bind("TAB",()->{if(screen==Screen.PLAY && overlay==Overlay.NONE && !game.getActiveCustomers().isEmpty()){
            int i=game.getActiveCustomers().indexOf(selected);selected=game.getActiveCustomers().get((i+1)%game.getActiveCustomers().size());
        }});
        setFocusTraversalKeysEnabled(false);
        for(int i=0;i<6;i++){final int index=i;bind(Integer.toString(i+1),()->{
            if(screen==Screen.PLAY && overlay==Overlay.NONE)pickOrRefill(game.getLevel().getHerbs().get(index));
        });}
        repaintTimer=new javax.swing.Timer(33,e->{
            if(screen==Screen.PLAY && game.isGameOver())screen=Screen.RESULT;
            if(!game.getActiveCustomers().contains(selected))selected=game.getActiveCustomers().isEmpty()?null:game.getActiveCustomers().get(0);
            repaint();
        });
    }
    private void bind(String key,Runnable action){
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),key);
        getActionMap().put(key,new AbstractAction(){@Override public void actionPerformed(ActionEvent e){action.run();repaint();}});
    }
    @Override public void addNotify(){super.addNotify();repaintTimer.start();}
    @Override public void removeNotify(){repaintTimer.stop();game.stop();super.removeNotify();}
    public void openLevel(int index){if(game.loadLevel(index)){screen=Screen.BRIEF;selected=null;hint="";overlay=Overlay.NONE;}}
    public void startStage(boolean realtime){screen=Screen.PLAY;if(realtime)game.start();else game.startManual();}
    public void openBook(){bookPage=0;openOverlay(Overlay.BOOK);}
    public void pauseForFocusLoss(){if(screen==Screen.PLAY && overlay==Overlay.NONE && game.isRunning())openOverlay(Overlay.PAUSE);}
    public void shutdown(){repaintTimer.stop();game.stop();}
    private void serveSelected(){if(selected!=null)game.serveCustomer(selected);}
    private void openOverlay(Overlay next){
        overlayResumes=screen==Screen.PLAY && game.isRunning() && !game.isPaused();
        if(overlayResumes)game.pause();overlay=next;
    }
    private void closeOverlay(){overlay=Overlay.NONE;if(overlayResumes && game.isPaused())game.pause();overlayResumes=false;}
    private void toMap(){game.stop();overlay=Overlay.NONE;screen=Screen.MAP;}
    private void exitGame(){
        shutdown();
        Window window=SwingUtilities.getWindowAncestor(this);
        if(window!=null)window.dispatchEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSING));
    }

    @Override protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);Graphics2D g=(Graphics2D)graphics.create();
        scale=Math.min(getWidth()/(double)WIDTH,getHeight()/(double)HEIGHT);
        offsetX=(getWidth()-WIDTH*scale)/2;offsetY=(getHeight()-HEIGHT*scale)/2;
        g.translate(offsetX,offsetY);g.scale(scale,scale);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(INK);g.fillRect(0,0,WIDTH,HEIGHT);areas.clear();
        if(screen==Screen.PLAY && game.isGameOver())screen=Screen.RESULT;
        if(screen==Screen.TITLE)drawTitle(g);else if(screen==Screen.MAP)drawMap(g);else if(screen==Screen.BRIEF)drawBrief(g);else drawShop(g);
        if(screen==Screen.RESULT)drawResult(g);
        if(overlay!=Overlay.NONE){areas.clear();drawOverlay(g);}
        g.dispose();
    }
    private void drawTitle(Graphics2D g){
        if(titleBackground==null){
            // Cache a softly blurred room once; keep text and buttons sharp.
            int padding=16,w=WIDTH/2,h=HEIGHT/2;
            BufferedImage room=new BufferedImage(w+padding*2,h+padding*2,BufferedImage.TYPE_INT_RGB);
            Graphics2D bg=room.createGraphics();
            bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            BufferedImage art=AssetManager.getBackgroundNoCounter();
            bg.drawImage(art!=null?art:PixelArt.shop(0),0,0,room.getWidth(),room.getHeight(),null);
            BufferedImage counter=AssetManager.getCounterLayer();
            if(counter!=null)bg.drawImage(counter,0,padding+(int)(h*.72),room.getWidth(),(int)(h*.28)+padding,null);
            bg.dispose();
            float[] weights=new float[17];double sum=0;
            for(int i=0;i<weights.length;i++){weights[i]=(float)Math.exp(-Math.pow(i-8,2)/32.0);sum+=weights[i];}
            for(int i=0;i<weights.length;i++)weights[i]/=(float)sum;
            BufferedImage horizontal=new java.awt.image.ConvolveOp(new java.awt.image.Kernel(17,1,weights),java.awt.image.ConvolveOp.EDGE_NO_OP,null).filter(room,null);
            BufferedImage blurred=new java.awt.image.ConvolveOp(new java.awt.image.Kernel(1,17,weights),java.awt.image.ConvolveOp.EDGE_NO_OP,null).filter(horizontal,null);
            titleBackground=blurred.getSubimage(padding,padding,w,h);
        }
        Graphics2D menu=(Graphics2D)g.create();
        menu.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        menu.drawImage(titleBackground,0,0,WIDTH,HEIGHT,null);
        menu.setPaint(new GradientPaint(0,0,new Color(28,16,29,95),0,HEIGHT,new Color(28,16,29,185)));
        menu.fillRect(0,0,WIDTH,HEIGHT);
        menu.setFont(new Font("Georgia",Font.BOLD,66));
        String name="Sweet Potion cafe";int x=(WIDTH-menu.getFontMetrics().stringWidth(name))/2;
        menu.setColor(new Color(31,16,24,180));menu.drawString(name,x+3,343);
        menu.setColor(PAPER);menu.drawString(name,x,340);
        menu.setColor(GOLD);menu.drawLine(465,375,735,375);
        menu.fillPolygon(new int[]{600,606,600,594},new int[]{369,375,381,375},4);
        button(menu,"เริ่มเล่น",450,424,300,60,this::toMap,true,true);
        button(menu,"ออก",450,506,300,60,this::exitGame,true,false);
        menu.dispose();
    }
    private void text(Graphics2D g,String s,int x,int y,int size,Color c){
        g.setColor(c);g.setFont(new Font("Tahoma",Font.PLAIN,size));g.drawString(s,x,y);
    }
    private void title(Graphics2D g,String s,int x,int y,int size,Color c){
        g.setColor(c);g.setFont(new Font("Georgia",Font.BOLD,size));g.drawString(s,x,y);
    }
    private void fittedText(Graphics2D g,String s,int x,int y,int width,int size,Color color){
        while(size>8){
            g.setFont(new Font("Tahoma",Font.PLAIN,size));
            if(g.getFontMetrics().stringWidth(s)<=width)break;
            size--;
        }
        text(g,s,x,y,size,color);
    }
    private Color moodColor(CustomerMood mood){
        return mood==CustomerMood.ANGRY?RED:mood==CustomerMood.IMPATIENT?GOLD:TEAL;
    }
    private void centered(Graphics2D g,String s,int x,int y,int w,int size,Color c){
        g.setFont(new Font("Tahoma",Font.PLAIN,size));g.setColor(c);g.drawString(s,x+(w-g.getFontMetrics().stringWidth(s))/2,y);
    }
    private void wrap(Graphics2D g,String s,int x,int y,int width,int size,Color c){
        g.setFont(new Font("Tahoma",Font.PLAIN,size));g.setColor(c);
        StringBuilder line=new StringBuilder();int yy=y;
        // Thai does not use word spaces consistently; retain combining marks with their base.
        java.text.BreakIterator breaks=java.text.BreakIterator.getCharacterInstance(Locale.forLanguageTag("th"));breaks.setText(s);
        int start=breaks.first();
        for(int end=breaks.next();end!=java.text.BreakIterator.DONE;start=end,end=breaks.next()){
            String ch=s.substring(start,end);
            if(g.getFontMetrics().stringWidth(line+ch)>width){g.drawString(line.toString(),x,yy);yy+=size+6;line.setLength(0);}
            line.append(ch);
        }
        g.drawString(line.toString(),x,yy);
    }
    private void box(Graphics2D g,int x,int y,int w,int h,Color fill,Color border){
        g.setColor(new Color(0x21151d));g.fillRect(x+3,y+4,w,h);
        g.setColor(fill);g.fillRect(x,y,w,h);g.setColor(border);g.drawRect(x,y,w,h);
        g.fillRect(x,y,4,4);g.fillRect(x+w-3,y,4,4);g.fillRect(x,y+h-3,4,4);g.fillRect(x+w-3,y+h-3,4,4);
    }
    private void button(Graphics2D g,String s,int x,int y,int w,int h,Runnable action,boolean enabled,boolean primary){
        boolean hover=enabled && new Rectangle(x,y,w,h).contains(hoverX,hoverY);
        Color base=primary?GOLD:new Color(0x5c3c38);
        box(g,x,y,w,h,enabled?(hover?base.brighter():base):new Color(0x3a2b2d),enabled?(primary?new Color(0xffdf9c):new Color(0xb58a65)):new Color(0x68504a));
        centered(g,s,x,y+h/2+6,w,15,enabled?(primary?INK:PAPER):new Color(0x9a8272));
        if(enabled)areas.add(new HitArea(x,y,w,h,action));
    }
    private void journalButton(Graphics2D g,String label,int x,int y,int w,boolean guests,Runnable action){
        NotebookArt.closed(g,x,y,w,38,guests,new Rectangle(x,y,w,38).contains(hoverX,hoverY));
        text(g,label,x+49,y+25,13,PAPER);
        areas.add(new HitArea(x,y,w,43,action));
    }
    private void star(Graphics2D g,int x,int y,int radius,boolean earned){
        int[] xs=new int[10],ys=new int[10];
        for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;double r=i%2==0?radius:radius*.45;xs[i]=x+(int)(Math.cos(a)*r);ys[i]=y+(int)(Math.sin(a)*r);}
        g.setColor(earned?GOLD:new Color(0x475752));g.fillPolygon(xs,ys,10);
    }
    private void header(Graphics2D g,String stageName){
        fittedText(g,stageName,28,56,486,28,PAPER);
        g.setColor(new Color(0x95664f));g.drawLine(20,83,1180,83);
    }
    private final BufferedImage mapParchment=AssetManager.read("asset/map/parchment.png");
    private final BufferedImage[] mapLocations={
        AssetManager.read("asset/map/forest.png"),AssetManager.read("asset/map/harbor.png"),
        AssetManager.read("asset/map/cave.png"),AssetManager.read("asset/map/palace.png")
    };
    // Landmarks and the scenic trail are deliberately staggered across the parchment.
    private static final int[][] MAP_ROUTE={
        {165,300},{235,326},{310,300},{365,240},{440,190},
        {550,175},{635,220},{700,290},{775,365},{920,415},
        {820,475},{700,510},{600,490},{510,530},{450,610},
        {550,675},{650,700},{760,680},{865,710},{990,690}
    };
    private static final int[][] MAP_LANDMARKS={{245,190},{925,275},{300,540},{925,560}};
    private void drawMapTerrain(Graphics2D g){
        Graphics2D ink=(Graphics2D)g.create();
        ink.setColor(new Color(116,75,39,105));
        ink.setStroke(new BasicStroke(1.6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        int[][] mountains={{495,125,22},{530,133,30},{566,124,19},
            {400,390,25},{439,405,37},{478,397,25},
            {180,630,20},{210,646,28},{245,640,19},
            {995,145,22},{1026,155,30}};
        for(int[] m:mountains){
            int x=m[0],y=m[1],s=m[2];
            ink.drawPolyline(new int[]{x-s,x-5,x,x+s},new int[]{y+s,y+6,y,y+s},4);
            ink.drawPolyline(new int[]{x-8,x-2,x+3,x+8},new int[]{y+11,y+14,y+9,y+16},4);
            ink.drawLine(x+3,y+12,x+s-8,y+s-3);
            for(int k=0;k<3;k++)ink.drawLine(x+8+k*4,y+20+k*5,x+12+k*4,y+24+k*5);
            ink.drawLine(x-s-4,y+s+5,x+s+6,y+s+3);
        }
        // Small cave mouths and broken rock contours, as if sketched by a traveler.
        for(int[] c:new int[][]{{595,350},{780,570}}){
            int x=c[0],y=c[1];
            ink.drawPolyline(new int[]{x-28,x-20,x-9,x+3,x+19,x+29},
                new int[]{y+24,y+2,y-9,y-5,y+5,y+24},6);
            ink.drawArc(x-12,y+3,25,34,0,180);
            ink.drawLine(x-12,y+20,x-12,y+29);ink.drawLine(x+13,y+20,x+13,y+29);
            ink.drawLine(x-20,y+29,x+22,y+31);
            ink.drawLine(x-19,y+5,x-12,y);ink.drawLine(x+18,y+10,x+24,y+20);
        }
        for(int[] tree:new int[][]{{150,425},{174,442},{198,416},{335,130},{355,145},{1040,500},{1062,523}}){
            int x=tree[0],y=tree[1];
            ink.drawPolyline(new int[]{x-9,x,x+9,x-12,x,x+12},
                new int[]{y+10,y-9,y+10,y+22,y+1,y+22},6);
            ink.drawLine(x,y+22,x,y+29);
        }
        for(int row=0;row<3;row++)for(int col=0;col<3;col++){
            int x=1015+col*20,y=360+row*13;
            ink.drawArc(x,y,20,7,180,160);
        }
        ink.dispose();
    }
    private void drawMapTrail(Graphics2D g){
        java.awt.geom.Path2D trail=new java.awt.geom.Path2D.Double();
        trail.moveTo(MAP_ROUTE[0][0],MAP_ROUTE[0][1]);
        // Catmull-Rom control points keep the scenic road smooth.
        for(int i=0;i<MAP_ROUTE.length-1;i++){
            int[] a=MAP_ROUTE[Math.max(0,i-1)],b=MAP_ROUTE[i],
                c=MAP_ROUTE[i+1],d=MAP_ROUTE[Math.min(MAP_ROUTE.length-1,i+2)];
            trail.curveTo(b[0]+(c[0]-a[0])/6.0,b[1]+(c[1]-a[1])/6.0,
                c[0]-(d[0]-b[0])/6.0,c[1]-(d[1]-b[1])/6.0,c[0],c[1]);
        }
        Graphics2D road=(Graphics2D)g.create();
        road.setColor(new Color(153,102,49,28));
        road.setStroke(new BasicStroke(12,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));road.draw(trail);
        road.setColor(new Color(126,79,35,170));
        road.setStroke(new BasicStroke(2.6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1,new float[]{2,9},0));road.draw(trail);
        road.dispose();
    }
    private void drawMap(Graphics2D g){
        if(mapParchment!=null)g.drawImage(mapParchment,0,0,WIDTH,HEIGHT,null);
        else {g.setColor(new Color(0xe9bf7d));g.fillRect(0,0,WIDTH,HEIGHT);}
        drawMapTerrain(g);drawMapTrail(g);
        journalButton(g,"ตำรา [B]",723,20,135,false,this::openBook);
        journalButton(g,"ลูกค้า",870,20,130,true,()->{guestPage=0;openOverlay(Overlay.GUESTS);});
        button(g,"วิธีเล่น",1012,20,158,38,()->openOverlay(Overlay.HELP),true,false);
        Color mapInk=new Color(0x56351f);
        for(int world=0;world<Level.WORLD_COUNT;world++){
            int cx=MAP_LANDMARKS[world][0],cy=MAP_LANDMARKS[world][1];
            BufferedImage art=mapLocations[world];
            if(art!=null){
                double fit=Math.min(190.0/art.getWidth(),130.0/art.getHeight());
                int w=(int)(art.getWidth()*fit),h=(int)(art.getHeight()*fit);
                g.drawImage(art,cx-w/2,cy-h/2,w,h,null);
            }
            boolean hover=new Rectangle(cx-110,cy-75,220,178).contains(hoverX,hoverY);
            centered(g,Level.WORLDS[world],cx-130,cy+86,260,18,hover?new Color(0x9e5824):mapInk);
            int total=0;
            for(int i=0;i<Level.STAGES_PER_WORLD;i++)total+=game.getProgress().getStars(world*Level.STAGES_PER_WORLD+i);
            centered(g,"5 ด่าน · "+total+" / 15 ดาว",cx-130,cy+108,260,12,mapInk);
            final int targetWorld=world;
            areas.add(new HitArea(cx-110,cy-75,220,190,()->{selectedWorld=targetWorld;openOverlay(Overlay.STAGES);}));
        }
        centered(g,"กดที่สถานที่เพื่อเลือกเล่น 5 ด่าน    |    ดาวสะสม  "+game.getProgress().getTotalStars()+" / "+Level.MAX_STARS,140,761,920,13,mapInk);
        if(!game.getProgress().getError().isEmpty())
            centered(g,game.getProgress().getError(),100,785,1000,12,RED);
    }
    private String[] missionLabels(){
        Level l=game.getLevel();return new String[]{"เสิร์ฟลูกค้าอย่างน้อย "+l.getServeTarget()+" คน", "ทำยอดขายอย่างน้อย "+l.getGoldTarget()+" G",
            "ปรุงสำเร็จ "+l.getCleanTarget()+" ครั้ง + ไม่ปรุงพลาด / ไม่เสิร์ฟผิด"};
    }
    private void drawBrief(Graphics2D g){
        Level l=game.getLevel();header(g,l.getName());
        drawShopPreview(g,30,106,1140,260,l.getWorld());
        box(g,184,295,832,441,PANEL,new Color(0x8e9a76));
        text(g,"CHAPTER "+l.getWorld()+"  /  STAGE "+String.format("%02d",l.getStage()),220,333,12,GOLD);
        text(g,l.getName(),220,372,29,PAPER);
        text(g,"ลูกค้า "+l.getCustomerCount()+" คน  •  เวลา "+(int)l.getDuration()+" วินาที  •  สกิลเช็คสุ่ม 1–5 ครั้งตามระดับลูกค้า",220,405,15,MUTED);
        String[] labels=missionLabels();for(int i=0;i<3;i++){star(g,234,441+i*32,10,true);text(g,labels[i],258,447+i*32,16,PAPER);}
        text(g,"วัตถุดิบชนิดละ 5 ชิ้น · หมดแล้วกดช่องเพื่อเติม รอ 3 วินาที",220,558,14,MUTED);
        for(int i=0;i<6;i++){Ingredient h=l.getHerbs().get(i);int x=220+i*126;
            PixelArt.herb(g,h,x+38,581,1);centered(g,h.getThaiName(),x-8,632,124,12,PAPER);
        }
        button(g,"กลับแผนที่",220,665,170,43,this::toMap,true,false);
        button(g,"เปิดตำรายา",405,665,170,43,this::openBook,true,false);
        button(g,"เปิดร้าน [ENTER]",697,665,280,43,()->startStage(true),true,true);
        centered(g,"ได้อย่างน้อย 1 ดาวเมื่อจบด่าน = ผ่าน  •  เล่นซ้ำเพื่อเก็บครบ 3 ดาวได้",0,783,WIDTH,14,MUTED);
    }
    private void drawShopPreview(Graphics2D g,int x,int y,int w,int h,int world){
        BufferedImage bg=AssetManager.getBackgroundNoCounter();
        if(bg==null){g.drawImage(PixelArt.shop(world),x,y,w,h,null);return;}
        // Crop to the room above the counter, preserving the original artwork's scale.
        int sh=(int)Math.round(bg.getWidth()*h/(double)w);
        sh=Math.min(sh,550);
        g.drawImage(bg,x,y,x+w,y+h,0,0,bg.getWidth(),sh,null);
    }
    private void drawShop(Graphics2D g){
        Level l=game.getLevel();
        BufferedImage room=AssetManager.getBackgroundNoCounter();
        if(room!=null)g.drawImage(room,0,SCENE_Y,WIDTH,SCENE_H,null);
        else g.drawImage(PixelArt.shop(l.getWorld()),0,SCENE_Y,WIDTH,SCENE_H,null);
        double time=System.nanoTime()/1_000_000_000.0;
        List<Customer> customers=game.getActiveCustomers();
        if(!customers.contains(selected))selected=customers.isEmpty()?null:customers.get(0);
        // Render the whole customer first. The foreground counter subsequently
        // occludes the lower body, so selection and serving share its visible area.
        for(int i=0;i<GameEngine.MAX_CUSTOMER_SLOTS;i++){
            int x=30+i*290;
            if(i<customers.size()){
                Customer c=customers.get(i);
                int bob=game.isPaused()?0:(int)(Math.sin(time*1.7+i)*2);
                // Transparent top padding preserves relative heights and all headwear.
                // Visitors share one floor behind the counter at y=660.
                PixelArt.customer(g,c,x-9,378+bob,6);
            }
        }
        BufferedImage counter=AssetManager.getCounterLayer();
        if(counter!=null)g.drawImage(counter,0,COUNTER_Y,WIDTH,SCENE_Y+SCENE_H-COUNTER_Y,null);
        else{
            g.setColor(new Color(0x97563b));g.fillRect(0,COUNTER_Y,WIDTH,195);
            g.setColor(GOLD);g.fillRect(0,COUNTER_Y,WIDTH,4);
        }
        // Compact parchment speech bubbles leave faces and headwear visible.
        for(int i=0;i<customers.size();i++){
            Customer c=customers.get(i);int x=30+i*290;
            int orderY=268;
            boolean active=c==selected;
            drawOrderBubble(g,x,orderY,c,active);
            if(screen==Screen.PLAY)areas.add(new HitArea(x,orderY,270,COUNTER_Y-orderY,()->{
                selected=c;if(game.getCurrentHeldPotion()!=null)game.serveCustomer(c);
            }));
        }
        drawHerbBox(g);
        drawBrewingStation(g,time);
        drawOrder(g);
        // Compact status and objectives leave the room itself visible.
        g.setColor(INK);g.fillRect(0,0,WIDTH,89);
        header(g,l.getName());
        drawHudStat(g,false,Integer.toString(game.getGold()),544,106,GOLD);
        drawHudStat(g,true,HudArt.time(game.getTimeRemaining()),658,94,game.getTimeRemaining()<15?RED:PAPER);
        journalButton(g,"ตำรา [B]",760,27,136,false,this::openBook);
        button(g,"พัก [ESC]",907,27,120,38,()->openOverlay(Overlay.PAUSE),screen==Screen.PLAY,false);
        button(g,SoundManager.isSoundEnabled()?"เสียง: เปิด":"เสียง: ปิด",1038,27,132,38,()->SoundManager.setSoundEnabled(!SoundManager.isSoundEnabled()),true,false);
        drawMissions(g);
        g.setColor(INK);g.fillRect(0,781,WIDTH,39);
        Graphics2D notice=(Graphics2D)g.create();notice.clipRect(24,781,884,39);
        text(notice,game.getMessage(),24,806,13,game.getCurrentHeldPotion()!=null && game.getCurrentHeldPotion().isFailed()?RED:PAPER);
        notice.dispose();
        text(g,"ลูกค้า "+game.getSpawnedCount()+"/"+l.getCustomerCount()+"  ·  ชื่อเสียง "+game.getReputation()+"/5",932,805,13,MUTED);
    }
    private void drawHudStat(Graphics2D g,boolean clock,String value,int x,int width,Color color){
        Graphics2D stat=(Graphics2D)g.create();
        stat.clipRect(x,23,width,46);
        stat.setColor(new Color(0x38242b));stat.fillRect(x,23,width,46);
        stat.setColor(new Color(0x82583f));stat.fillRect(x+3,67,width-6,2);
        HudArt.draw(stat,clock,x+5,30,32);
        fittedText(stat,value,x+43,51,width-47,18,color);
        stat.dispose();
    }
    private void pickOrRefill(Ingredient herb){
        if(game.getIngredientStock(herb)==0)game.refillIngredient(herb);
        else game.addIngredient(herb);
    }
    private void drawIngredientPile(Graphics2D g,Ingredient herb,int x,int y,int count){
        if(count==0)return;
        BufferedImage sprite=HerbSprites.get(herb);
        Graphics2D pile=(Graphics2D)g.create();pile.clipRect(x,y,99,33);
        pile.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Each stock unit is a serving from a densely packed tray. Stable scattered
        // layers thin out as servings are taken; no random movement between frames.
        int backgroundPieces=(count-1)*9;
        for(int layer=0;layer<backgroundPieces;layer++){
            // Interleave the columns so partial stock leaves natural gaps throughout.
            int slot=(layer*13)%36,row=slot/12,col=slot%12;
            int seed=slot*37+herb.ordinal()*19;
            int cx=-3+col*9+(seed%5)-2,cy=-1+row*15+((seed/5)%7)-3;
            int size=32+(seed%3)*2;
            Graphics2D piece=(Graphics2D)pile.create();
            piece.translate(x+cx,y+cy);
            piece.rotate(((seed%13)-6)*.22);
            piece.drawImage(sprite,-size/2,-size/2,size,size,null);piece.dispose();
        }
        // The upright, unobscured central ingredient is always painted on top.
        pile.drawImage(sprite,x+33,y-1,34,34,null);
        pile.dispose();
    }
    private void drawHerbBox(Graphics2D g){
        // Keep the entire cabinet within the counter, including its lower trim.
        final double cabinetScale=.84;
        final int cabinetX=47,cabinetY=585;
        Graphics2D cabinetGraphics=(Graphics2D)g.create();
        cabinetGraphics.translate(cabinetX,cabinetY);
        cabinetGraphics.scale(cabinetScale,cabinetScale);
        cabinetGraphics.translate(-24,-553);
        g=cabinetGraphics;
        BufferedImage cabinet=AssetManager.getHerbBox();
        if(cabinet!=null)g.drawImage(cabinet,24,553,356,224,null);
        else box(g,24,553,356,224,new Color(0x78452f),GOLD);
        List<Ingredient> mix=game.getCauldron().getCurrentIngredients();
        boolean enabled=screen==Screen.PLAY && game.getSkillCheck()==null && game.getCurrentHeldPotion()==null;
        List<Ingredient> herbs=game.getLevel().getHerbs();
        for(int i=0;i<herbs.size();i++){
            Ingredient h=herbs.get(i);int x=40+(i%3)*111,y=565+(i/3)*61;
            Rectangle target=new Rectangle(cabinetX+(int)Math.round((x-24)*cabinetScale),
                cabinetY+(int)Math.round((y-553)*cabinetScale),
                (int)Math.round(99*cabinetScale),(int)Math.round(51*cabinetScale));
            boolean inMix=mix.contains(h),hover=target.contains(hoverX,hoverY);
            Graphics2D bin=(Graphics2D)g.create();bin.clipRect(x,y,99,51);
            bin.setColor(new Color(36,22,26,170));bin.fillRect(x,y,99,51);
            int count=game.getIngredientStock(h);
            boolean refilling=game.isRefilling(h);
            drawIngredientPile(bin,h,x,y,count);
            if(count==0){
                centered(bin,refilling?String.format(Locale.ROOT,"เติม %.1f วิ",game.getRefillRemaining(h)):"กดเติม",x+18,y+23,63,12,GOLD);
                if(refilling){
                    bin.setColor(new Color(0x593b38));bin.fillRect(x+20,y+28,61,3);
                    bin.setColor(TEAL);bin.fillRect(x+20,y+28,(int)Math.round(61*(1-game.getRefillRemaining(h)/GameEngine.REFILL_SECONDS)),3);
                }
            }
            bin.setColor(new Color(42,25,27,235));bin.fillRect(x,y+33,99,18);
            centered(bin,h.getThaiName(),x,y+47,99,11,enabled?PAPER:MUTED);
            bin.dispose();
            if(inMix || (enabled && hover)){
                g.setColor(inMix?TEAL:GOLD);g.drawRect(x,y,99,51);g.drawRect(x+1,y+1,97,49);
            }
            g.setColor(new Color(0xecd3a0));g.fillRect(x+2,y+2,14,15);
            centered(g,Integer.toString(i+1),x+2,y+14,14,10,INK);
            g.setColor(new Color(42,25,27,240));g.fillRect(x+77,y+2,21,13);
            text(g,Integer.toString(game.getIngredientStock(h))+"/5",x+79,y+12,9,PAPER);
            if((enabled || game.getIngredientStock(h)==0) && !game.isRefilling(h))areas.add(new HitArea(target.x,target.y,target.width,target.height,()->pickOrRefill(h)));
        }
        centered(g,"ตู้สมุนไพร  ·  กด 1–6",41,716,320,15,new Color(0x40232a));
        centered(g,"หมดแล้วกดเติม · รอ 3 วินาที",41,745,320,11,new Color(0xfce0ae));
        cabinetGraphics.dispose();
    }
    private void drawBrewingStation(Graphics2D g,double time){
        // The pot rests directly on the counter; only temporary brew states use a panel.
        g.setColor(new Color(29,16,22,110));g.fillOval(499,681,180,17);
        BufferedImage cauldron=CauldronSprites.frameAt((long)(time*1000));
        g.drawImage(cauldron,500,528,180,170,null);
        List<Ingredient> mix=game.getCauldron().getCurrentIngredients();
        for(int i=0;i<3;i++){
            int x=414+i*119;
            g.setColor(new Color(45,26,29,235));g.fillRect(x,700,112,26);
            if(i<mix.size())centered(g,mix.get(i).getThaiName(),x,718,112,11,PAPER);
            else centered(g,"+",x,719,112,15,MUTED);
        }
        Potion held=game.getCurrentHeldPotion();SkillCheck skill=game.getSkillCheck();
        if(held!=null){
            box(g,411,561,359,132,PANEL,GOLD);
            drawPotionArt(g,held,427,571,58,58);
            text(g,held.isFailed()?"ยาเสีย":"พร้อมเสิร์ฟ",496,589,16,held.isFailed()?RED:TEAL);
            wrap(g,held.getThaiName(),496,617,255,18,PAPER);
            text(g,held.isFailed()?"เททิ้งเพื่อปรุงใหม่":"คลิกลูกค้า หรือกด ENTER",496,674,12,MUTED);
        }
        if(skill!=null){
            // Draw on a small raster so every edge scales as a crisp, three-pixel step.
            BufferedImage bar=new BufferedImage(120,18,BufferedImage.TYPE_INT_ARGB);
            Graphics2D p=bar.createGraphics();
            p.setColor(new Color(0x231922));p.fillRect(0,5,120,8);p.fillRect(2,3,116,12);
            p.setColor(new Color(0x9b754d));p.fillRect(2,5,116,8);p.fillRect(4,4,112,10);
            p.setColor(new Color(0xf0cf8e));p.fillRect(4,4,112,2);
            p.setColor(new Color(0x493b40));p.fillRect(4,6,112,6);
            int targetX=4+(int)Math.round((skill.getTarget()-skill.getWidth()/2)*112);
            int targetW=(int)Math.round(skill.getWidth()*112);
            p.setColor(new Color(0x719950));p.fillRect(targetX,6,targetW,6);
            p.setColor(new Color(0xc4e291));p.fillRect(targetX,6,targetW,2);
            int cursor=4+(int)Math.round(skill.getPosition()*112);
            p.setColor(new Color(0x231922));p.fillRect(cursor-2,0,5,18);
            p.setColor(PAPER);p.fillRect(cursor-1,1,3,16);
            p.dispose();g.drawImage(bar,411,621,360,54,null);
            if(screen==Screen.PLAY)areas.add(new HitArea(411,609,360,78,game::skillHit));
            return;
        }
        button(g,"ปรุงยา [SPACE]",411,739,239,36,()->{
            if(game.getSkillCheck()==null)game.beginBrew(selected);else game.skillHit();
        },held==null && screen==Screen.PLAY,true);
        button(g,"เททิ้ง [R]",661,739,109,36,()->{game.trashHeldPotion();game.clearCauldron();},skill==null && screen==Screen.PLAY,false);
    }
    private void drawOrderBubble(Graphics2D g,int x,int y,Customer customer,boolean selected){
        Potion potion=customer.getDesiredPotion();
        Graphics2D q=(Graphics2D)g.create();
        q.translate(x,y);
        q.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        // The stair-step corners and bent tail echo a classic pixel speech balloon.
        Polygon outline=new Polygon(
            new int[]{12,258,258,264,264,270,270,264,264,258,94,94,88,88,80,80,60,60,66,66,72,72,12,12,6,6,0,0,6,6,12},
            new int[]{0,0,6,6,12,12,76,76,82,82,82,94,94,102,102,108,108,102,102,96,96,82,82,76,76,70,70,12,12,6,6},31);
        q.translate(3,4);q.setColor(new Color(0x4a3024));q.fillPolygon(outline);q.translate(-3,-4);
        Shape oldClip=q.getClip();q.clip(outline);
        q.setPaint(new GradientPaint(0,0,new Color(0xfff0cf),0,108,new Color(0xd9bc8b)));
        q.fillPolygon(outline);
        q.setColor(new Color(selected?0x987044:0xb08b60));
        q.setStroke(new BasicStroke(8,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER));q.drawPolygon(outline);
        q.setClip(oldClip);
        // Fine inset highlights suggest aged parchment without adding extra content.
        q.setColor(new Color(0xfff5dc));q.fillRect(17,8,236,2);
        q.setColor(new Color(0xc6a271));q.fillRect(16,74,48,2);q.fillRect(102,74,151,2);
        double patience=Math.max(0,Math.min(1,customer.getPatiencePercentage()));
        q.setColor(new Color(0x98744e));q.fillRect(20,16,230,10);
        q.setColor(new Color(0xcbb38c));q.fillRect(22,18,226,6);
        q.setColor(new Color(patience>.5?0x8b9d64:patience>.25?0xc69a50:0xb8664d));
        int patienceWidth=(int)Math.round(226*patience);
        q.fillRect(22,18,patienceWidth,6);
        if(patienceWidth>0){q.setColor(new Color(255,244,213,85));q.fillRect(22,18,patienceWidth,2);}
        drawPotionArt(q,potion,17,28,34,42);
        fittedText(q,potion.getThaiName(),56,57,194,16,new Color(0x59402c));
        q.dispose();
    }
    private void drawOrder(Graphics2D g){
        box(g,851,593,326,182,new Color(0xefd7ab),new Color(0x9b603f));
        Color ink=new Color(0x51352f),soft=new Color(0x81604e);
        text(g,"ออเดอร์",867,620,20,ink);text(g,"TAB เปลี่ยนลูกค้า",1040,618,11,soft);
        g.setColor(new Color(0xc3986b));g.drawLine(867,630,1161,630);
        if(selected==null){
            wrap(g,"ลูกค้ากำลังเดินทางมา รอรับออเดอร์ได้เลย",867,658,294,15,soft);return;
        }
        fittedText(g,selected.getName(),867,650,294,16,ink);
        fittedText(g,selected.getTitle()+" · "+selected.getMood().getLabel(),867,666,294,10,soft);
        Potion p=selected.getDesiredPotion();fittedText(g,p.getThaiName(),867,691,294,19,ink);
        Graphics2D trait=(Graphics2D)g.create();trait.clipRect(867,698,294,24);
        text(trait,selected.getPersonality(),867,714,11,soft);trait.dispose();
        button(g,"เสิร์ฟยา [ENTER]",867,732,294,30,this::serveSelected,
            screen==Screen.PLAY && game.getCurrentHeldPotion()!=null && !game.getCurrentHeldPotion().isFailed(),true);
    }
    private void drawMissions(Graphics2D g){
        boolean[] met=game.getMissions();String[] labels=missionLabels();
        String[] status={game.getTotalCustomersServed()+" / "+game.getLevel().getServeTarget()+" คน",game.getGold()+" / "+game.getLevel().getGoldTarget()+" G",
            game.getCleanBrews()+" / "+game.getLevel().getCleanTarget()+" ครั้ง  •  พลาด "+game.getMistakes()};
        for(int i=0;i<3;i++){
            int x=24+i*389;
            g.setColor(new Color(42,27,31,225));g.fillRect(x,103,374,57);
            g.setColor(new Color(0x9b7659));g.drawRect(x,103,374,57);
            star(g,x+17,123,9,met[i]);
            text(g,labels[i],x+34,124,i==2?11:13,met[i]?GOLD:PAPER);
            text(g,status[i],x+34,147,12,met[i]?TEAL:MUTED);
        }
    }
    private void shade(Graphics2D g){g.setColor(new Color(8,18,24,224));g.fillRect(0,0,WIDTH,HEIGHT);}
    private void drawResult(Graphics2D g){
        areas.clear();shade(g);int stars=game.getEarnedStars();
        box(g,260,182,680,456,PANEL,GOLD);
        centered(g,stars>0?"ร้านของคุณสร้างความประทับใจ!":"วันนี้ยังไม่ใช่วันของเรา",280,231,640,26,PAPER);
        for(int i=0;i<3;i++)star(g,510+i*90,288,30,i<stars);
        centered(g,stars>0?"ผ่านด่าน  •  "+stars+" / 3 ดาว":"ยังไม่ผ่าน  •  ต้องได้อย่างน้อย 1 ดาว",280,350,640,21,stars>0?TEAL:RED);
        String[] labels=missionLabels();boolean[] met=game.getMissions();
        for(int i=0;i<3;i++){star(g,309,392+i*32,9,met[i]);text(g,labels[i],332,398+i*32,15,met[i]?PAPER:MUTED);}
        text(g,"เสิร์ฟ "+game.getTotalCustomersServed()+" คน  /  ออกไป "+game.getTotalCustomersLost()+" คน  /  ยอดขาย "+game.getGold()+" G",308,509,15,GOLD);
        int index=game.getLevel().getIndex();
        button(g,"แผนที่",301,550,170,45,this::toMap,true,false);
        button(g,"เล่นซ้ำ",486,550,170,45,()->openLevel(index),true,false);
        button(g,index==Level.TOTAL_LEVELS-1?"จบการเดินทาง":"ด่านถัดไป",671,550,225,45,()->{if(index==Level.TOTAL_LEVELS-1)toMap();else openLevel(index+1);},stars>0,true);
        String note=game.getProgress().getError();
        centered(g,note.isEmpty()?(index==Level.TOTAL_LEVELS-1 && stars>0?"เดินทางครบ "+Level.TOTAL_LEVELS+" ด่านแล้ว! กลับไปเก็บดาวให้ครบ "+Level.MAX_STARS+" ดวงได้":"ดาวสูงสุดถูกบันทึกแล้ว  •  กลับมาเก็บดาวเพิ่มได้เสมอ"):note,280,625,640,12,note.isEmpty()?MUTED:RED);
    }
    private void drawOverlay(Graphics2D g){
        shade(g);
        if(overlay==Overlay.BOOK){drawBook(g);return;}
        if(overlay==Overlay.GUESTS){drawGuests(g);return;}
        if(overlay==Overlay.STAGES){drawLocationStages(g);return;}
        box(g,240,159,720,491,PANEL,GOLD);
        if(overlay==Overlay.PAUSE){
            centered(g,"พักจิบชา",240,228,720,32,PAPER);
            centered(g,"เวลาของร้านและความอดทนลูกค้าหยุดอยู่",240,278,720,18,MUTED);
            button(g,"กลับไปปรุงยา [ESC]",390,327,420,48,this::closeOverlay,true,true);
            button(g,"ตำรายา",390,397,200,43,()->{overlay=Overlay.BOOK;bookPage=0;},true,false);
            button(g,"วิธีเล่น",610,397,200,43,()->overlay=Overlay.HELP,true,false);
            button(g,"กลับแผนที่ / ออกจากด่าน",390,486,420,43,this::toMap,true,false);
            centered(g,"ดาวจะบันทึกเมื่อจบด่านเท่านั้น",240,590,720,14,MUTED);
        }else{
            centered(g,"คู่มือนักปรุงยา",240,215,720,28,PAPER);
            String[] lines={"1  คลิกลูกค้าเพื่อดูสูตรและนิสัยเฉพาะตัว", "2  คลิกสมุนไพร หรือกด 1–6 ใส่หม้อ 2–3 ชนิด",
                "3  กด SPACE เริ่มปรุง แล้วปล่อย SPACE ในแถบสีเขียว", "4  ผ่านสกิลเช็คทุกจังหวะ แล้วคลิกลูกค้าเพื่อเสิร์ฟ",
                "5  ถ้ายาเสีย กด R เททิ้ง แล้วเริ่มปรุงใหม่", "6  ผ่านอย่างน้อย 1 ภารกิจดาวก่อนร้านปิดเพื่อปลดล็อกด่าน"};
            for(int i=0;i<lines.length;i++)text(g,lines[i],277,265+i*41,16,PAPER);
            text(g,"B ตำรายา  /  TAB เปลี่ยนลูกค้า  /  ENTER เสิร์ฟ  /  ESC พัก",277,548,15,GOLD);
            text(g,"เปิดตำราหรือออกจากหน้าต่างเกม เวลาจะหยุดให้",277,582,14,MUTED);
            button(g,"เข้าใจแล้ว [ESC]",390,605,420,35,this::closeOverlay,true,true);
        }
    }
    private void drawLocationStages(Graphics2D g){
        Color ink=new Color(0x56351f),faded=new Color(0x977451);
        box(g,170,150,860,520,new Color(0xeac58b),new Color(0x80512c));
        g.setColor(new Color(0xb18b53));g.drawRect(183,163,834,494);
        centered(g,Level.WORLDS[selectedWorld],190,212,820,28,ink);
        BufferedImage art=mapLocations[selectedWorld];
        if(art!=null){
            double fit=Math.min(160.0/art.getWidth(),110.0/art.getHeight());
            int w=(int)(art.getWidth()*fit),h=(int)(art.getHeight()*fit);
            g.drawImage(art,600-w/2,232+(110-h)/2,w,h,null);
        }
        centered(g,"เลือกด่านที่ต้องการเล่น",190,372,820,16,ink);
        for(int stage=0;stage<Level.STAGES_PER_WORLD;stage++){
            int index=selectedWorld*Level.STAGES_PER_WORLD+stage,x=210+stage*160,y=395;
            boolean unlocked=game.getProgress().isUnlocked(index);
            boolean hover=unlocked && new Rectangle(x,y,140,132).contains(hoverX,hoverY);
            g.setColor(new Color(0x80512c));g.fillRect(x+2,y+3,140,132);
            g.setColor(unlocked?(hover?new Color(0xffe2a5):new Color(0xffd68c)):new Color(0xc8ab7d));g.fillRect(x,y,140,132);
            g.setColor(unlocked?ink:faded);g.drawRect(x,y,140,132);
            centered(g,"ด่าน "+(stage+1),x,y+34,140,21,unlocked?ink:faded);
            centered(g,(selectedWorld+1)+"-"+(stage+1),x,y+59,140,13,unlocked?ink:faded);
            int earned=game.getProgress().getStars(index);
            for(int k=0;k<3;k++)star(g,x+45+k*25,y+83,9,k<earned);
            centered(g,unlocked?"เล่นด่านนี้":"ยังไม่ปลดล็อก",x,y+116,140,13,unlocked?ink:faded);
            if(unlocked){final int target=index;areas.add(new HitArea(x,y,140,132,()->openLevel(target)));}
        }
        centered(g,"ผ่านด่านก่อนหน้าอย่างน้อย 1 ดาว เพื่อปลดล็อกด่านถัดไป",190,562,820,14,ink);
        button(g,"กลับแผนที่ [ESC]",450,595,300,43,this::closeOverlay,true,false);
    }
    private void drawBook(Graphics2D g){
        journalFrame(g,false,"The Herbalist's Almanac","ตำรายาประจำร้าน",bookPage);
        text(g,"24 สูตร · สมุนไพรต่างชนิด 2–3 ชนิด",139,165,13,NotebookArt.FADED);
        text(g,screen==Screen.MAP?"เลือกด่านเพื่อดูสูตรที่ปรุงได้":"วงกลมเขียว = ปรุงได้ในด่านนี้",643,165,13,NotebookArt.GREEN);
        List<Potion> recipes=RecipeBook.getAllRecipes();
        for(int k=0;k<8;k++){
            int index=bookPage*8+k;if(index>=recipes.size())break;
            Potion p=recipes.get(index);int x=139+(k%2)*504,y=198+(k/2)*117;
            boolean available=screen!=Screen.MAP && game.getLevel().getHerbs().containsAll(p.getRecipe());
            g.setColor(new Color(0xe6cda0));g.fillRect(x,y+8,47,67);
            drawPotionArt(g,p,x+3,y+11,43,57);
            text(g,String.format("%02d",index+1),x+12,y+94,11,NotebookArt.FADED);
            fittedText(g,p.getThaiName(),x+60,y+26,322,17,NotebookArt.INK);
            text(g,p.getEnglishName(),x+60,y+45,11,NotebookArt.FADED);
            g.setColor(available?NotebookArt.GREEN:NotebookArt.RULE);g.fillOval(x+394,y+14,7,7);
            int hi=0;for(Ingredient h:p.getRecipe()){
                int hx=x+60+hi*116;
                PixelArt.herb(g,h,hx,y+54,1);
                fittedText(g,h.getThaiName(),hx+35,y+75,78,11,NotebookArt.INK);hi++;
            }
            g.setColor(NotebookArt.RULE);g.drawLine(x,y+108,x+411,y+108);
        }
        journalNavigation(g,bookPage,3,()->bookPage--,()->bookPage++);
    }
    private void drawPotionArt(Graphics2D target,Potion potion,int x,int y,int width,int height){
        BufferedImage image=PotionSprites.get(potion);
        if(image==null){PixelArt.bottle(target,x,y,Math.max(1,width/12),potion.getLiquidColor());return;}
        Graphics2D g=(Graphics2D)target.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image,x,y,width,height,null);g.dispose();
    }
    private void drawGuests(Graphics2D g){
        journalFrame(g,true,"Visitors of the Atelier","บันทึกผู้มาเยือน",guestPage);
        text(g,"13 ผู้มาเยือน · จดจำใบหน้าและนิสัย",139,165,13,NotebookArt.FADED);
        text(g,"ความอดทน: ปกติ > 50% · เริ่มไม่พอใจ ≤ 50% · โกรธ ≤ 25%",643,165,11,NotebookArt.FADED);
        CustomerProfile[] profiles=CustomerProfile.values();
        for(int k=0;k<4;k++){
            int index=guestPage*4+k,x=139+(k%2)*504,y=198+(k/2)*228;
            if(index>=profiles.length){
                NotebookArt.icon(g,x+190,y+65,true);
                centered(g,"รอเรื่องราวจากผู้มาเยือนคนต่อไป…",x,y+120,412,14,NotebookArt.FADED);continue;
            }
            CustomerProfile p=profiles[index];
            fittedText(g,p.getName(),x,y+22,410,18,NotebookArt.INK);
            text(g,p.getTitle(),x,y+41,11,NotebookArt.FADED);
            for(CustomerMood mood:CustomerMood.values()){
                int sx=x+30+mood.ordinal()*132;
                g.setColor(new Color(0xdfc69b));g.fillOval(sx+12,y+136,74,9);
                PixelArt.customer(g,p,mood,sx,y+49,3);
                centered(g,mood.getLabel(),sx-6,y+159,108,11,NotebookArt.INK);
            }
            text(g,p.getRarity().getTierName()+" · "+p.getHeightCm()+" cm · รอพื้นฐาน "+(int)p.getRarity().getDefaultPatienceSeconds()+" วินาที",x,y+180,11,NotebookArt.FADED);
            fittedText(g,p.getTrait(),x,y+202,412,12,NotebookArt.GREEN);
            g.setColor(NotebookArt.RULE);g.drawLine(x,y+216,x+412,y+216);
        }
        journalNavigation(g,guestPage,4,()->guestPage--,()->guestPage++);
    }
    private void journalFrame(Graphics2D g,boolean guests,String english,String thai,int page){
        NotebookArt.spread(g);
        NotebookArt.icon(g,139,106,guests);
        title(g,english,174,125,22,NotebookArt.INK);
        text(g,thai,643,125,24,NotebookArt.INK);
        for(int x:new int[]{139,643}){
            g.setColor(NotebookArt.RULE);g.drawLine(x,142,x+411,142);
            g.fillRect(x,139,5,7);g.fillRect(x+406,139,5,7);
        }
        // Protruding index tabs switch sections without disturbing the pause state.
        for(int i=0;i<2;i++){
            final boolean nextGuests=i==1;int y=183+i*81;
            boolean active=guests==nextGuests,hover=new Rectangle(1092,y,63,65).contains(hoverX,hoverY);
            g.setColor(new Color(0x3b2922));g.fillRect(1091,y+3,66,65);
            g.setColor(new Color(active?(hover?0xb3bc90:0x93a079):(hover?0xe5c18c:0xc9a271)));g.fillRect(1092,y,60,63);
            NotebookArt.icon(g,1110,y+9,nextGuests);
            centered(g,nextGuests?"ลูกค้า":"น้ำยา",1093,y+51,58,11,NotebookArt.INK);
            areas.add(new HitArea(1092,y,63,65,()->{overlay=nextGuests?Overlay.GUESTS:Overlay.BOOK;}));
        }
        centered(g,"— "+(page*2+1)+" —",139,677,412,12,NotebookArt.FADED);
        centered(g,"— "+(page*2+2)+" —",643,677,412,12,NotebookArt.FADED);
    }
    private void journalNavigation(Graphics2D g,int page,int count,Runnable previous,Runnable next){
        pageTurn(g,false,page>0,previous);
        centered(g,(page+1)+" / "+count,310,716,190,14,NotebookArt.FADED);
        pageTurn(g,true,page<count-1,next);
        journalClose(g);
    }
    private void pageTurn(Graphics2D g,boolean next,boolean enabled,Runnable action){
        int x=next?1020:110;
        Rectangle target=new Rectangle(x,650,70,70);
        boolean hover=target.contains(hoverX,hoverY);
        NotebookArt.pageCorner(g,next,enabled,hover);
        if(hover && enabled)text(g,next?"หน้าถัดไป":"หน้าก่อนหน้า",next?954:185,686,12,NotebookArt.FADED);
        if(enabled)areas.add(new HitArea(x,650,70,70,action));
    }
    private void journalClose(Graphics2D g){
        Rectangle target=new Rectangle(1140,16,48,48);
        boolean hover=target.contains(hoverX,hoverY);
        Graphics2D q=(Graphics2D)g.create();
        q.setStroke(new BasicStroke(4,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER));
        int radius=hover?12:10;
        q.setColor(new Color(0x21151d));
        q.drawLine(1164-radius+2,40-radius+3,1164+radius+2,40+radius+3);
        q.drawLine(1164+radius+2,40-radius+3,1164-radius+2,40+radius+3);
        q.setColor(Color.WHITE);
        q.drawLine(1164-radius,40-radius,1164+radius,40+radius);
        q.drawLine(1164+radius,40-radius,1164-radius,40+radius);
        q.dispose();
        if(hover)text(g,"เก็บสมุด [ESC]",1032,45,13,PAPER);
        areas.add(new HitArea(target.x,target.y,target.width,target.height,this::closeOverlay));
    }
}
