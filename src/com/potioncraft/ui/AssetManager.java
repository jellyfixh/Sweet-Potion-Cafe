package com.potioncraft.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

/**
 * Manages loading and caching of game graphical assets.
 */
public final class AssetManager {
    private static final Path PROJECT_ROOT = findProjectRoot();
    private static BufferedImage backgroundNoCounter;
    private static BufferedImage counterLayer;
    private static BufferedImage herbBox;
    private static boolean loaded = false;

    private AssetManager() {}

    private static Path findProjectRoot() {
        try {
            Path location = Paths.get(AssetManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // A packaged game must use its own artwork, never a nearby stale asset folder.
            if (!Files.isDirectory(location)) return null;
            for (Path directory = location; directory != null; directory = directory.getParent()) {
                if (Files.isRegularFile(directory.resolve("src/com/potioncraft/Main.java"))
                        && Files.isDirectory(directory.resolve("asset/customers"))) return directory;
            }
            // Some IDEs put compiled classes in their own cache outside the project.
            for (Path directory = Paths.get("").toAbsolutePath(); directory != null; directory = directory.getParent()) {
                if (Files.isRegularFile(directory.resolve("src/com/potioncraft/Main.java"))
                        && Files.isDirectory(directory.resolve("asset/customers"))) return directory;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot locate game artwork", ex);
        }
        return null;
    }

    public static BufferedImage read(String path) {
        Path file = PROJECT_ROOT == null ? null : PROJECT_ROOT.resolve(path);
        try (InputStream in = file != null && Files.isRegularFile(file) ? Files.newInputStream(file)
                : AssetManager.class.getResourceAsStream("/" + path)) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException ex) {
            System.err.println("Cannot load " + path + ": " + ex.getMessage());
            return null;
        }
    }

    public static synchronized void loadAssets() {
        if (loaded) return;
        backgroundNoCounter = read("asset/background/background_no_counter.png");
        BufferedImage counter = read("asset/background/counter_layer.png");
        if (counter != null) {
            // Both layers share the same canvas; only the foreground starts here.
            int top = (int)Math.round(counter.getHeight() * 550.0 / 768.0);
            counterLayer = counter.getSubimage(0, top, counter.getWidth(), counter.getHeight() - top);
        }
        herbBox = read("asset/background/herb_box_cutout.png");
        loaded = true;
    }

    public static BufferedImage getBackgroundNoCounter() {
        if (!loaded) loadAssets();
        return backgroundNoCounter;
    }

    public static BufferedImage getCounterLayer() {
        if (!loaded) loadAssets();
        return counterLayer;
    }

    public static BufferedImage getHerbBox() {
        if (!loaded) loadAssets();
        return herbBox;
    }

}
