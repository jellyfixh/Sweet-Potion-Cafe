package com.potioncraft.tools;

import com.potioncraft.model.potion.Ingredient;
import com.potioncraft.ui.HerbSprites;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import javax.imageio.ImageIO;

/** Exports the current authored sprites without overwriting live game artwork. */
public final class PrepareHerbArtwork {
    public static void main(String[] args)throws Exception{
        Path out=Paths.get("output/herbs");Files.createDirectories(out);
        BufferedImage sheet=new BufferedImage(800,360,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=sheet.createGraphics();g.setColor(new Color(0x30232b));g.fillRect(0,0,800,360);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setFont(new Font("Tahoma",Font.PLAIN,12));
        StringBuilder csv=new StringBuilder("id,thai_name,file,width,height\n");
        try(ZipOutputStream zip=new ZipOutputStream(Files.newOutputStream(out.resolve("herbs-transparent-8.zip")))){
            for(Ingredient herb:Ingredient.values()){
                BufferedImage image=HerbSprites.get(herb);
                Path asset=Paths.get(HerbSprites.assetPath(herb));
                zip.putNextEntry(new ZipEntry(asset.getFileName().toString()));Files.copy(asset,zip);zip.closeEntry();
                int x=herb.ordinal()%4*200,y=herb.ordinal()/4*180;
                g.setColor(new Color(0x4d3439));g.fillRect(x+8,y+8,184,164);
                g.drawImage(image,x+52,y+12,96,96,null);
                g.setColor(new Color(0xf6deb4));g.drawString(herb.getThaiName(),x+18,y+135);g.drawString(herb.getEnglishName(),x+18,y+154);
                csv.append(herb.name()).append(',').append(herb.getThaiName()).append(',').append(HerbSprites.assetPath(herb)).append(",32,32\n");
            }
        }
        g.dispose();ImageIO.write(sheet,"png",out.resolve("herbs-preview.png").toFile());
        Files.writeString(out.resolve("manifest.csv"),"\uFEFF"+csv,StandardCharsets.UTF_8);
        System.out.println("Packaged eight current 32x32 ingredient PNGs, preview, manifest and ZIP.");
    }
}
