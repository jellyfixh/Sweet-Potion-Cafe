package com.potioncraft.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Transparent, lantern-lit pixel sprites; each half of the sheet is one icon. */
final class HudArt {
    private static final BufferedImage SHEET = AssetManager.read("asset/ui/hud-icons.png");
    private static final BufferedImage COIN = sprite(0);
    private static final BufferedImage CLOCK = sprite(1);

    private HudArt() {}

    private static BufferedImage sprite(int index) {
        if (SHEET == null) throw new IllegalStateException("Missing HUD icon artwork");
        int start = index * SHEET.getWidth() / 2;
        int end = (index + 1) * SHEET.getWidth() / 2;
        int left = end, top = SHEET.getHeight(), right = start, bottom = 0;
        for (int y = 0; y < SHEET.getHeight(); y++) {
            for (int x = start; x < end; x++) {
                if ((SHEET.getRGB(x, y) >>> 24) > 32) {
                    left = Math.min(left, x); right = Math.max(right, x);
                    top = Math.min(top, y); bottom = Math.max(bottom, y);
                }
            }
        }
        if (left > right || top > bottom) throw new IllegalStateException("Empty HUD sprite");
        return SHEET.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    static void draw(Graphics2D g, boolean clock, int x, int y, int size) {
        BufferedImage icon = clock ? CLOCK : COIN;
        double scale = Math.min(size / (double) icon.getWidth(), size / (double) icon.getHeight());
        int w = (int) Math.round(icon.getWidth() * scale), h = (int) Math.round(icon.getHeight() * scale);
        g.drawImage(icon, x + (size - w) / 2, y + (size - h) / 2, w, h, null);
    }

    static String time(double seconds) {
        int remaining = (int) Math.ceil(Math.max(0, seconds));
        return (remaining / 60) + ":" + (remaining % 60);
    }
}
