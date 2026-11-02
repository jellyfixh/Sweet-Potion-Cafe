package com.potioncraft.model.game;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/** Saves only personal bests. A replay cannot remove earned stars. */
public final class CampaignProgress {
    private final int[] stars=new int[Level.TOTAL_LEVELS];
    private final Path path;
    private String error="";
    public CampaignProgress(Path path){this.path=path;load();}
    private void load(){
        if(!Files.exists(path))return;
        Properties p=new Properties();
        try(InputStream in=Files.newInputStream(path)){
            p.load(in);
            // Saves without layout metadata belong to the original 4 x 10 campaign.
            boolean legacy=!Integer.toString(Level.STAGES_PER_WORLD).equals(p.getProperty("stagesPerWorld"));
            for(int i=0;i<stars.length;i++){
                int savedIndex=legacy?(i/Level.STAGES_PER_WORLD)*10+i%Level.STAGES_PER_WORLD:i;
                try{stars[i]=Math.min(3,Math.max(0,Integer.parseInt(p.getProperty("level."+savedIndex,"0"))));}
                catch(NumberFormatException ignored){stars[i]=0;}
            }
        }catch(IOException ex){error="อ่านบันทึกไม่ได้: "+ex.getMessage();}
    }
    public boolean isUnlocked(int i){return i>=0 && i<stars.length && (i==0 || stars[i-1]>=1);}
    public int getStars(int i){return stars[i];}
    public int getTotalStars(){int sum=0;for(int s:stars)sum+=s;return sum;}
    public void record(int index,int earned){
        if(!isUnlocked(index))throw new IllegalArgumentException("Stage is locked");
        stars[index]=Math.max(stars[index],Math.min(3,Math.max(0,earned)));
        Properties p=new Properties();
        p.setProperty("stagesPerWorld",Integer.toString(Level.STAGES_PER_WORLD));
        for(int i=0;i<stars.length;i++)p.setProperty("level."+i,Integer.toString(stars[i]));
        try{
            Path absolute=path.toAbsolutePath();Files.createDirectories(absolute.getParent());
            Path temp=absolute.resolveSibling(absolute.getFileName()+".tmp");
            try(OutputStream out=Files.newOutputStream(temp)){p.store(out,"Potion Atelier campaign v2 (4 x 5)");}
            try{Files.move(temp,absolute,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException ex){Files.move(temp,absolute,StandardCopyOption.REPLACE_EXISTING);}
            error="";
        }catch(IOException ex){error="บันทึกไม่ได้: "+ex.getMessage();}
    }
    public String getError(){return error;}
}
