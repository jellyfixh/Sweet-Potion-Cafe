import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import javax.imageio.ImageIO;

/** Packs generated art into crisp, separate native game icons; does not touch live game assets. */
public class PackageIngredientIcons {
    static final String[] KEYS={"mermaids_teardrop","millennial_root","rainbow_crystal","moonlight_dust","frost_petal","luminous_shroom","sunflower_pollen","dragon_scale"};
    static final String[] NAMES={"หยดน้ำตานางเงือก","รากไม้พันปี","คริสตัลสีรุ้ง","ละอองแสงจันทร์","กลีบดอกเหมันต์","เห็ดเรืองแสง","เกสรดอกสุริยัน","เกล็ดมังกร"};
    static final Path ROOT=Paths.get("output/ingredients-v2");
    static BufferedImage normalize(BufferedImage raw){
        int minX=raw.getWidth(),minY=raw.getHeight(),maxX=0,maxY=0;
        for(int y=0;y<raw.getHeight();y++)for(int x=0;x<raw.getWidth();x++)if((raw.getRGB(x,y)>>>24)>=128){
            minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);
        }
        int rw=maxX-minX+1,rh=maxY-minY+1;
        double scale=Math.min(28.0/rw,28.0/rh);
        int w=(int)Math.round(rw*scale),h=(int)Math.round(rh*scale),ox=(32-w)/2,oy=(32-h)/2;
        BufferedImage result=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){
            int sx=minX+Math.min(rw-1,(int)((x+.5)*rw/w)),sy=minY+Math.min(rh-1,(int)((y+.5)*rh/h));
            int color=raw.getRGB(sx,sy);
            if((color>>>24)>=128)result.setRGB(ox+x,oy+y,0xff000000|(color&0xffffff));
        }
        return result;
    }
    public static void main(String[] args)throws Exception{
        BufferedImage sheet=new BufferedImage(1024,560,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=sheet.createGraphics();g.setColor(new Color(0x241c28));g.fillRect(0,0,1024,560);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setColor(new Color(0xf5dfb5));g.setFont(new Font("Tahoma",Font.BOLD,21));g.drawString("POTION ATELIER / MAGICAL INGREDIENTS",24,32);
        StringBuilder csv=new StringBuilder("id,thai_name,width,height,transparent,black\n");
        for(int i=0;i<KEYS.length;i++){
            BufferedImage raw=ImageIO.read(ROOT.resolve("source/"+KEYS[i]+".png").toFile());
            if(!raw.getColorModel().hasAlpha() || (raw.getRGB(0,0)>>>24)>=128)throw new IllegalStateException("Expected true source transparency: "+KEYS[i]);
            BufferedImage sprite=normalize(raw);ImageIO.write(sprite,"png",ROOT.resolve("transparent/"+KEYS[i]+".png").toFile());
            BufferedImage black=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);Graphics2D b=black.createGraphics();b.drawImage(sprite,0,0,null);b.dispose();
            ImageIO.write(black,"png",ROOT.resolve("black/"+KEYS[i]+".png").toFile());
            int x=16+(i%4)*252,y=52+(i/4)*250;
            g.setColor(new Color(0x3f2b32));g.fillRect(x,y,240,236);
            g.drawImage(sprite,x+56,y+10,128,128,null);
            // Actual-size sample next to the enlarged pixel preview.
            g.drawImage(sprite,x+102,y+148,null);
            g.setColor(new Color(0xf4dfba));g.setFont(new Font("Tahoma",Font.PLAIN,15));g.drawString(NAMES[i],x+14,y+201);
            g.setColor(new Color(0xbfa7a0));g.setFont(new Font("Tahoma",Font.PLAIN,12));g.drawString(KEYS[i]+"  /  32 x 32",x+14,y+222);
            csv.append(KEYS[i]).append(',').append(NAMES[i]).append(",32,32,transparent/").append(KEYS[i]).append(".png,black/").append(KEYS[i]).append(".png\n");
        }
        g.dispose();ImageIO.write(sheet,"png",ROOT.resolve("ingredients-preview.png").toFile());
        Files.writeString(ROOT.resolve("manifest.csv"),"\uFEFF"+csv,StandardCharsets.UTF_8);
        try(ZipOutputStream zip=new ZipOutputStream(Files.newOutputStream(ROOT.resolve("magical-ingredients-8.zip")))){
            for(String folder:new String[]{"transparent","black"})for(String key:KEYS){
                String name=folder+"/"+key+".png";zip.putNextEntry(new ZipEntry(name));Files.copy(ROOT.resolve(name),zip);zip.closeEntry();
            }
            for(String name:new String[]{"ingredients-preview.png","manifest.csv","prompts.txt"}){
                zip.putNextEntry(new ZipEntry(name));Files.copy(ROOT.resolve(name),zip);zip.closeEntry();
            }
        }
        System.out.println("Verified and packaged eight 32x32 alpha sprites, eight black variants, preview and manifest.");
    }
}
