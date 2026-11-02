package com.potioncraft.test;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.game.*;
import com.potioncraft.model.customer.*;
import com.potioncraft.ui.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.Random;

/** Paints real Swing components and dispatches real input, without opening a window. */
public final class VisualSmokeTest {
    private static Path out;
    private static BufferedImage snapshot(PotionGamePanel panel,String name,int width,int height)throws Exception{
        panel.setSize(width,height);BufferedImage img=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=img.createGraphics();panel.paint(g);g.dispose();ImageIO.write(img,"png",out.resolve(name+".png").toFile());
        return img;
    }
    private static void click(PotionGamePanel p,int x,int y){p.dispatchEvent(new MouseEvent(p,MouseEvent.MOUSE_PRESSED,System.currentTimeMillis(),0,x,y,1,false));}
    public static void main(String[] args)throws Exception{
        SoundManager.setSoundEnabled(false);UITheme.setupGlobalThaiFont();out=Paths.get("build","screenshots");Files.createDirectories(out);
        SwingUtilities.invokeAndWait(()->{
            try{
                GameEngine game=new GameEngine(Files.createTempDirectory(Paths.get("build"),"visual-test-").resolve("save.properties"),new Random(4));
                PotionGamePanel panel=new PotionGamePanel(game);
                BufferedImage title=snapshot(panel,"00-title",1200,820);
                panel.getActionMap().get("B").actionPerformed(new java.awt.event.ActionEvent(panel,0,"B"));
                CampaignTest.check(java.util.Arrays.equals(title.getRGB(0,0,1200,820,null,0,1200),snapshot(panel,"00-title",1200,820).getRGB(0,0,1200,820,null,0,1200)),"Title stays clear of gameplay overlays");
                CampaignTest.check(!game.isRunning(),"Opening menu does not start the game clock");
                snapshot(panel,"00-small-title",960,690);
                click(panel,480,17+(int)(454*.8));
                BufferedImage map=snapshot(panel,"01-map",1200,820);click(panel,245,190);
                CampaignTest.check(!java.util.Arrays.equals(title.getRGB(0,0,1200,820,null,0,1200),map.getRGB(0,0,1200,820,null,0,1200)),"Start button opens the map after resizing");
                BufferedImage stages=snapshot(panel,"25-forest-stages",1200,820);
                click(panel,440,450);BufferedImage locked=snapshot(panel,"26-locked-stage",1200,820);
                CampaignTest.check(java.util.Arrays.equals(stages.getRGB(0,0,1200,820,null,0,1200),locked.getRGB(0,0,1200,820,null,0,1200)),"Locked stage does not leave the location chooser");
                click(panel,600,615);BufferedImage closed=snapshot(panel,"27-location-closed",1200,820);
                CampaignTest.check(java.util.Arrays.equals(map.getRGB(0,0,1200,820,null,0,1200),closed.getRGB(0,0,1200,820,null,0,1200)),"Closing location restores the map");
                click(panel,245,190);snapshot(panel,"25-forest-stages",1200,820);click(panel,280,450);
                CampaignTest.check(game.getLevel().getIndex()==0,"Location stage selection works");snapshot(panel,"02-brief",1200,820);
                panel.startStage(false);game.advance(1);BufferedImage shop=snapshot(panel,"03-shop",1200,820);
                BufferedImage expected=new BufferedImage(1200,820,BufferedImage.TYPE_INT_RGB);
                Graphics2D layer=expected.createGraphics();
                layer.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                layer.drawImage(AssetManager.getBackgroundNoCounter(),0,90,1200,686,null);
                CampaignTest.check(shop.getRGB(600,240)==expected.getRGB(600,240),"Supplied room is visible in the shop");
                layer.drawImage(AssetManager.getCounterLayer(),0,581,1200,195,null);layer.dispose();
                CampaignTest.check(shop.getRGB(398,584)==expected.getRGB(398,584),"Counter foreground covers customer lower bodies");
                Customer c=game.getActiveCustomers().get(0);
                for(com.potioncraft.model.potion.Ingredient h:c.getDesiredPotion().getRecipe()){
                    int index=game.getLevel().getHerbs().indexOf(h);click(panel,47+(int)Math.round((61+(index%3)*111)*.84),585+(int)Math.round((37+(index/3)*61)*.84));
                }
                CampaignTest.check(game.getCauldron().getCount()==c.getDesiredPotion().getRecipe().size(),"Herb mouse targets work");
                click(panel,500,754);CampaignTest.check(game.getSkillCheck()!=null,"Brew mouse button works");game.advance(.4);
                snapshot(panel,"04-skill",1200,820);
                panel.openBook();double remaining=game.getTimeRemaining();game.advance(5);
                CampaignTest.check(game.getTimeRemaining()==remaining,"Recipe overlay pauses clock");snapshot(panel,"05-book",1200,820);
                click(panel,1060,690);BufferedImage pageTwo=snapshot(panel,"06-book-page2",1200,820);
                click(panel,1060,690);BufferedImage lastPage=snapshot(panel,"07-book-page3",1200,820);
                CampaignTest.check(!sameJournalContent(pageTwo,lastPage),"Folded next-page corner changes the displayed recipes");
                click(panel,1060,690);BufferedImage boundary=snapshot(panel,"22-book-last-page-boundary",1200,820);
                CampaignTest.check(sameJournalContent(lastPage,boundary),"Last-page arrow does not navigate beyond the recipes");
                click(panel,140,690);BufferedImage previous=snapshot(panel,"23-book-previous-page",1200,820);
                CampaignTest.check(sameJournalContent(pageTwo,previous),"Left corner returns to the previous recipes");
                click(panel,1120,290);snapshot(panel,"17-guests-in-shop",1200,820);
                game.advance(5);CampaignTest.check(game.getTimeRemaining()==remaining,"Switching journal tabs keeps the shop paused");
                for(int page=2;page<=4;page++){click(panel,1060,690);snapshot(panel,"18-guests-page"+page,1200,820);}
                snapshot(panel,"19-small-journal",960,690);
                click(panel,(int)(140*.8),(int)(17+690*.8));snapshot(panel,"24-small-journal-previous",960,690);
                click(panel,(int)(1164*.8),(int)(17+40*.8));
                CampaignTest.check(!game.isPaused(),"Closing the resized guest journal resumes the shop");
                snapshot(panel,"20-shop-restored",1200,820);panel.openBook();snapshot(panel,"21-book-reopened",1200,820);
                click(panel,1164,40);CampaignTest.check(!game.isPaused(),"Floating close cross closes the book and resumes the shop");
                game.advance(6);snapshot(panel,"08-failed-brew",1200,820);
                game.reset();game.startManual();game.advance(110);snapshot(panel,"09-result",1200,820);
                click(panel,380,570);snapshot(panel,"10-small-map",960,690);
                snapshot(panel,"11-map-restored",1200,820);click(panel,920,45);snapshot(panel,"12-guests",1200,820);
                game.stop();panel.shutdown();
                GameEngine busy=new GameEngine(Files.createTempDirectory(Paths.get("build"),"visual-test-").resolve("save.properties"),new Random(4));
                PotionGamePanel room=new PotionGamePanel(busy);room.openLevel(0);room.startStage(false);busy.advance(26);
                CampaignTest.check(busy.getActiveCustomers().size()==4,"All four customer positions are exercised");
                snapshot(room,"13-full-shop",1200,820);
                snapshot(room,"14-small-shop",960,690);
                double scaled=.8,ox=0,oy=17;
                for(int i=0;i<6;i++){
                    busy.clearCauldron();click(room,(int)(ox+(47+(61+(i%3)*111)*.84)*scaled),(int)(oy+(585+(37+(i/3)*61)*.84)*scaled));
                    CampaignTest.check(busy.getCauldron().getCurrentIngredients().equals(java.util.List.of(busy.getLevel().getHerbs().get(i))),"All six herb compartments work after resizing");
                }
                busy.clearCauldron();
                Customer second=busy.getActiveCustomers().get(1);
                CampaignTest.completeBrew(busy,second.getDesiredPotion());
                snapshot(room,"15-ready-to-serve",1200,820);click(room,450,440);
                CampaignTest.check(second.isServed() && busy.getTotalCustomersServed()==1,"Clicking a visible customer serves that customer");
                snapshot(room,"16-wide-shop",1366,768);
                room.shutdown();
                GameEngine supplies=new GameEngine(Files.createTempDirectory(Paths.get("build"),"supply-visual-").resolve("save"),new Random(4));
                PotionGamePanel supplyPanel=new PotionGamePanel(supplies);supplyPanel.openLevel(0);supplyPanel.startStage(false);
                com.potioncraft.model.potion.Ingredient supply=supplies.getLevel().getHerbs().get(0);
                snapshot(supplyPanel,"29-supply-full",1200,820);
                for(int i=0;i<3;i++)click(supplyPanel,98,616);
                CampaignTest.check(supplies.getIngredientStock(supply)==2,"Three mouse picks leave two visible pieces");
                snapshot(supplyPanel,"30-supply-two-left",1200,820);supplies.clearCauldron();
                for(int i=0;i<2;i++)click(supplyPanel,98,616);
                supplies.clearCauldron();snapshot(supplyPanel,"31-supply-empty",1200,820);
                click(supplyPanel,98,616);CampaignTest.check(supplies.isRefilling(supply),"Click empty compartment starts refill");
                supplies.advance(1.5);snapshot(supplyPanel,"32-supply-refilling",1200,820);
                supplies.advance(1.5);CampaignTest.check(supplies.getIngredientStock(supply)==5,"Refill restores all five pieces after three seconds");
                snapshot(supplyPanel,"33-supply-restored",1200,820);
                for(int i=0;i<5;i++){supplies.addIngredient(supply);supplies.clearCauldron();}
                supplyPanel.getActionMap().get("1").actionPerformed(new java.awt.event.ActionEvent(supplyPanel,0,"1"));
                CampaignTest.check(supplies.isRefilling(supply),"Number key also refills an empty compartment");
                supplyPanel.shutdown();
                GameEngine mapGame=new GameEngine(Files.createTempDirectory(Paths.get("build"),"map-test-").resolve("save.properties"),new Random(4));
                for(int i=0;i<Level.TOTAL_LEVELS;i++)mapGame.getProgress().record(i,3);
                int[][] locations={{245,190},{925,275},{300,540},{925,560}};
                for(int i=0;i<Level.TOTAL_LEVELS;i++){
                    PotionGamePanel mapPanel=new PotionGamePanel(mapGame);
                    mapPanel.getActionMap().get("ENTER").actionPerformed(new java.awt.event.ActionEvent(mapPanel,0,"ENTER"));
                    mapPanel.setSize(960,690);
                    BufferedImage target=new BufferedImage(960,690,BufferedImage.TYPE_INT_RGB);
                    Graphics2D mapGraphics=target.createGraphics();mapPanel.paint(mapGraphics);mapGraphics.dispose();
                    int[] location=locations[i/5];
                    click(mapPanel,(int)(location[0]*.8),(int)(17+location[1]*.8));
                    mapGraphics=target.createGraphics();mapPanel.paint(mapGraphics);mapGraphics.dispose();
                    if(i==19)ImageIO.write(target,"png",out.resolve("28-small-palace-stages.png").toFile());
                    click(mapPanel,(int)((280+(i%5)*160)*.8),(int)(17+450*.8));
                    CampaignTest.check(mapGame.getLevel().getIndex()==i,"All location stage cards work after resizing: "+i);
                    mapPanel.shutdown();
                }
                System.out.println("PASS: Map nodes at resized dimensions, room/counter layers, herb targets, serving, journal pagination and rendered screenshots.");
            }catch(Exception ex){throw new RuntimeException(ex);}
        });
    }
    private static boolean sameJournalContent(BufferedImage a,BufferedImage b){
        return java.util.Arrays.equals(a.getRGB(139,198,412,457,null,0,412),b.getRGB(139,198,412,457,null,0,412));
    }
}
