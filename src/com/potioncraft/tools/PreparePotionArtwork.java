package com.potioncraft.tools;

import com.potioncraft.model.potion.Potion;
import com.potioncraft.model.potion.RecipeBook;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Converts the authored potion illustrations into compact transparent game sprites. */
public final class PreparePotionArtwork {
    private static final Map<String, String> SOURCE = new HashMap<>();
    static {
        SOURCE.put("ELIXIR_OF_THE_DEEP", "โพชั่นห้วงลึกมหาสมุทร.png");
        SOURCE.put("TIDAL_VITALITY", "โพชั่นวารีบริสุทธิ์.png");
        SOURCE.put("PRISM_PROTECTION", "โพชั่นโล่หนามมังกร.png");
        SOURCE.put("LUNAR_AURORA", "โพชั่นผลึกจันทรา.png");
        SOURCE.put("STARLIGHT_SLUMBER", "โพชั่นห้วงนิทรา.png");
        SOURCE.put("PERMAFROST_TONIC", "โพขั่นระเบิดน้ำแข็ง.png");
        SOURCE.put("SOLAR_RENEWAL", "โพชั่นเพลิงสุริยัน.png");
        SOURCE.put("FLAME_OF_DAWN", "โพชั่นแห่งรุ่งอรุณ.png");
        SOURCE.put("ANCIENT_BLAZE", "โพชั่นรากเพลิงดึกดำบรรพ์.png");
        SOURCE.put("MOONLIGHT_SPORE", "โพชั่นสปอร์เห็ดเรืองแสง.png");
        SOURCE.put("SIRENS_FROZEN_HEART", "โพชั่นหัวใจนางเงือกเยือกแข็ง.png");
        SOURCE.put("CELESTIAL_OCEAN_BREEZE", "โพชั่นสายลมดารา.png");
        SOURCE.put("LEVIATHANS_BLESSING", "โพชั่นพรแห่งเลเวียธาน.png");
        SOURCE.put("WORLD_TREE_SAP", "โพชั่นยางไม้พฤกษาโลก.png");
        SOURCE.put("WINTER_DRAGON_BREATH", "โพชั่นลมหายใจมังกรเหมันต์.png");
        SOURCE.put("SOLAR_PHOENIX_ELIXIR", "โพชั่นจุติฟีนิกซ์สุริยัน.png");
        SOURCE.put("ASTRAL_DREAMSCAPE", "โพชั่นมิติแห่งความฝัน.png");
        SOURCE.put("FROSTFIRE_AEGIS", "โพชั่นเกราะเพลิงเยือกแข็ง.png");
        SOURCE.put("MOONLIT_TIDE_TONIC", "โพชั่นกระแสวารีจันทร์กระจ่าง.png");
        SOURCE.put("ANCIENT_SUN_REMEDY", "โพชั่นโอสถสุริยะพันปี.png");
        SOURCE.put("DRAGON_ROOTS_DRAUGHT", "โพชั่นแก่นรากมังกร.png");
        SOURCE.put("GLACIAL_STARLIGHT_BREW", "โพชั่นเหมันต์หยุดเวลา.png");
        SOURCE.put("RAINBOW_MIRACLE_ELIXIR", "โพชั่นปาฏิหาริย์แห่งรุ้ง.png");
        SOURCE.put("ABYSSAL_DRAGON_TEARS", "โพชั่นน้ำตามังกรห้วงลึก.png");
    }

    private PreparePotionArtwork() {}

    public static void main(String[] args) throws IOException {
        Path root = Paths.get("");
        Path input = root.resolve("asset/potion");
        Path output = root.resolve("asset/potions");
        Files.createDirectories(output);
        for (Potion potion : RecipeBook.getAllRecipes()) {
            String filename = SOURCE.get(potion.getId());
            if (filename == null) throw new IOException("No source mapping for " + potion.getId());
            Path source = input.resolve(filename);
            BufferedImage image = ImageIO.read(source.toFile());
            if (image == null) throw new IOException("Cannot read potion artwork: " + source);
            BufferedImage sprite = makeSprite(image);
            Path target = output.resolve(potion.getId().toLowerCase(Locale.ROOT) + ".png");
            ImageIO.write(sprite, "png", target.toFile());
        }
        System.out.println("Prepared " + RecipeBook.getAllRecipes().size() + " transparent potion sprites.");
    }

    private static BufferedImage makeSprite(BufferedImage source) {
        int w = source.getWidth(), h = source.getHeight();
        int[] pixels = source.getRGB(0, 0, w, h, null, 0, w);
        boolean hasAlpha = false;
        for (int pixel : pixels) if ((pixel >>> 24) < 255) { hasAlpha = true; break; }
        boolean[] background = new boolean[w * h];
        if (hasAlpha) {
            for (int i = 0; i < pixels.length; i++) background[i] = (pixels[i] >>> 24) == 0;
        } else {
            int[] queue = new int[w * h];
            int head = 0, tail = 0;
            for (int x = 0; x < w; x++) { tail = enqueue(pixels, w, x, 0, background, queue, tail); tail = enqueue(pixels, w, x, h - 1, background, queue, tail); }
            for (int y = 1; y < h - 1; y++) { tail = enqueue(pixels, w, 0, y, background, queue, tail); tail = enqueue(pixels, w, w - 1, y, background, queue, tail); }
            while (head < tail) {
                int p = queue[head++], x = p % w, y = p / w;
                if (x > 0) tail = enqueue(pixels, w, x - 1, y, background, queue, tail);
                if (x + 1 < w) tail = enqueue(pixels, w, x + 1, y, background, queue, tail);
                if (y > 0) tail = enqueue(pixels, w, x, y - 1, background, queue, tail);
                if (y + 1 < h) tail = enqueue(pixels, w, x, y + 1, background, queue, tail);
            }
        }
        int minX = w, minY = h, maxX = -1, maxY = -1;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) if (!background[y * w + x]) {
            minX = Math.min(minX, x); minY = Math.min(minY, y); maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
        }
        BufferedImage result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double scale = Math.min(30.0 / (maxX - minX + 1), 30.0 / (maxY - minY + 1));
        int dw = Math.max(1, (int)Math.round((maxX - minX + 1) * scale));
        int dh = Math.max(1, (int)Math.round((maxY - minY + 1) * scale));
        int dx = (32 - dw) / 2, dy = (32 - dh) / 2;
        BufferedImage cut = new BufferedImage(maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_INT_ARGB);
        for (int y = minY; y <= maxY; y++) for (int x = minX; x <= maxX; x++) {
            int argb = pixels[y * w + x];
            if (background[y * w + x]) argb &= 0x00ffffff;
            cut.setRGB(x - minX, y - minY, argb);
        }
        g.drawImage(cut, dx, dy, dw, dh, null);
        g.dispose();
        return result;
    }

    private static int enqueue(int[] pixels, int width, int x, int y, boolean[] seen, int[] queue, int tail) {
        int index = y * width + x;
        if (seen[index]) return tail;
        int rgb = pixels[index], r = (rgb >>> 16) & 255, g = (rgb >>> 8) & 255, b = rgb & 255;
        if (Math.min(r, Math.min(g, b)) < 150 || Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b)) > 34) return tail;
        seen[index] = true;
        queue[tail++] = index;
        return tail;
    }
}
