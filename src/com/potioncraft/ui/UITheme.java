package com.potioncraft.ui;

import javax.swing.UIManager;
import java.awt.Font;
import java.util.Enumeration;

/**
 * UI Theme helper ensuring crisp Thai language typography and emoji support
 * across all Swing components on Windows.
 */
public class UITheme {
    public static final String THAI_FONT_FAMILY = "Tahoma";
    public static final String EMOJI_FONT_FAMILY = "Segoe UI Emoji";

    public static final Font FONT_HEADER = new Font(THAI_FONT_FAMILY, Font.BOLD, 20);
    public static final Font FONT_TITLE = new Font(THAI_FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font(THAI_FONT_FAMILY, Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font(THAI_FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font(THAI_FONT_FAMILY, Font.PLAIN, 10);
    public static final Font FONT_SMALL_BOLD = new Font(THAI_FONT_FAMILY, Font.BOLD, 10);

    public static final Font FONT_EMOJI_HUGE = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 42);
    public static final Font FONT_EMOJI_LARGE = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 32);
    public static final Font FONT_EMOJI_MED = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 22);
    public static final Font FONT_EMOJI_SMALL = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 14);

    /**
     * Overrides default Swing UIManager fonts with Tahoma for complete Thai character rendering.
     */
    public static void setupGlobalThaiFont() {
        Font defaultFont = new Font(THAI_FONT_FAMILY, Font.PLAIN, 12);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                Font current = (Font) value;
                UIManager.put(key, new Font(THAI_FONT_FAMILY, current.getStyle(), current.getSize()));
            }
        }
    }
}
