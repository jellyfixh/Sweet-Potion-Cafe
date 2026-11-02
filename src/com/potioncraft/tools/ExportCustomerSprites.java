package com.potioncraft.tools;

import com.potioncraft.model.customer.*;
import com.potioncraft.ui.CustomerSprites;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.*;
import java.io.*;

/** Packages authored artwork without regenerating or overwriting live sprites. */
public final class ExportCustomerSprites {
    private static final Path OUTPUT=Paths.get("output","customers");
    private static void png(BufferedImage image,Path path)throws IOException{
        Files.createDirectories(path.getParent());ImageIO.write(image,"png",path.toFile());
    }
    private static void zipFiles(Path zipPath,Path base,java.util.List<Path> paths)throws IOException{
        try(ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(zipPath))){
            for(Path path:paths){
                out.putNextEntry(new ZipEntry(base.relativize(path).toString().replace('\\','/')));
                Files.copy(path,out);out.closeEntry();
            }
        }
    }
    public static void main(String[] args)throws Exception{
        Files.createDirectories(OUTPUT);
        Path whiteRoot=OUTPUT.resolve("white"),alphaRoot=Paths.get("asset","customers");
        java.util.List<Path> whiteFiles=new java.util.ArrayList<>(),alphaFiles=new java.util.ArrayList<>();
        int fw=CustomerSprites.WIDTH,fh=CustomerSprites.HEIGHT;
        BufferedImage atlas=new BufferedImage(3*fw,CustomerProfile.values().length*fh,BufferedImage.TYPE_INT_ARGB);
        Graphics2D atlasG=atlas.createGraphics();
        StringBuilder csv=new StringBuilder("character,thai_name,english_name,rarity,height_cm,expression,thai_expression,width,height,white_file,transparent_file\n");
        for(CustomerProfile p:CustomerProfile.values())for(CustomerMood mood:CustomerMood.values()){
            BufferedImage sprite=CustomerSprites.get(p,mood);
            Path alpha=Paths.get(CustomerSprites.assetPath(p,mood));alphaFiles.add(alpha);
            BufferedImage white=new BufferedImage(fw,fh,BufferedImage.TYPE_INT_RGB);
            Graphics2D wg=white.createGraphics();wg.setColor(Color.WHITE);wg.fillRect(0,0,fw,fh);wg.drawImage(sprite,0,0,null);wg.dispose();
            Path whiteFile=whiteRoot.resolve(p.getAssetKey()+"-"+mood.getAssetKey()+".png");png(white,whiteFile);whiteFiles.add(whiteFile);
            atlasG.drawImage(sprite,mood.ordinal()*fw,p.ordinal()*fh,null);
            csv.append(p.getAssetKey()).append(',').append(p.getName()).append(',').append(p.getTitle()).append(',')
                .append(p.getRarity()).append(',').append(p.getHeightCm()).append(',').append(mood.getAssetKey()).append(',')
                .append(mood.getLabel()).append(',').append(fw).append(',').append(fh).append(',').append(whiteFile.toString().replace('\\','/')).append(',')
                .append(alpha.toString().replace('\\','/')).append('\n');
        }
        atlasG.dispose();png(atlas,OUTPUT.resolve("customer-atlas.png"));
        png(atlas,OUTPUT.resolve("customer-atlas-32.png"));
        Files.writeString(OUTPUT.resolve("manifest.csv"),"\uFEFF"+csv,StandardCharsets.UTF_8);
        preview();
        zipFiles(OUTPUT.resolve("customers-white-39.zip"),whiteRoot,whiteFiles);
        zipFiles(OUTPUT.resolve("customers-transparent-39.zip"),alphaRoot,alphaFiles);
        System.out.println("Packaged 39 native 32x32 PNGs, white-background exports, atlas, preview and two ZIPs.");
    }
    private static void label(Graphics2D g,String text,int x,int y,int size,Color color){
        g.setFont(new Font("Tahoma",Font.PLAIN,size));g.setColor(color);g.drawString(text,x,y);
    }
    private static void preview()throws IOException{
        BufferedImage sheet=new BufferedImage(1640,1640,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=sheet.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(0x2c202b));g.fillRect(0,0,sheet.getWidth(),sheet.getHeight());
        label(g,"POTION ATELIER  /  THE VISITORS",30,40,26,new Color(0xf2e7ca));
        label(g,"13 characters  ·  39 separate PNGs  ·  native 32 × 32  ·  simple 16-color palettes",30,68,16,new Color(0xd0b897));
        label(g,"ปกติ > 50%  /  ไม่พอใจ ≤ 50%  /  โกรธ ≤ 25% ของความอดทนที่เหลือ",30,93,15,new Color(0xe2b862));
        int[] columns=new int[4];
        for(CustomerRarity rarity:CustomerRarity.values()){
            int y=122+rarity.ordinal()*370;
            label(g,rarity.getTierName().toUpperCase(java.util.Locale.ROOT),30,y+20,15,rarity.getPrimaryColor().brighter());
        }
        for(CustomerProfile p:CustomerProfile.values()){
            int row=p.getRarity().ordinal(),col=columns[row]++,x=28+col*405,y=151+row*370;
            g.setColor(new Color(0xf6edda));g.fillRect(x,y,389,328);
            g.setColor(new Color(0xc79e6a));g.drawRect(x,y,389,328);
            label(g,p.getName(),x+12,y+23,16,new Color(0x48313b));
            label(g,p.getTitle()+"  ·  "+p.getHeightCm()+" cm",x+12,y+45,12,new Color(0x876a5a));
            for(CustomerMood mood:CustomerMood.values()){
                int sx=x+2+mood.ordinal()*129;
                g.drawImage(CustomerSprites.get(p,mood),sx,y+110,128,128,null);
                label(g,mood.getLabel(),sx+25,y+286,12,new Color(0x48313b));
                label(g,mood.getAssetKey(),sx+25,y+306,11,new Color(0x876a5a));
            }
        }
        label(g,"Chunky 32-pixel characters from the approved references. Common floor, varied heights and angry gestures.",30,1610,14,new Color(0xd0b897));
        g.dispose();png(sheet,OUTPUT.resolve("all-customers-three-expressions.png"));
    }
}
