package com.potioncraft;

import com.potioncraft.ui.PotionShopGUI;
import com.potioncraft.ui.UITheme;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Potion Craft Shop Game.
 */
public class Main {
    public static void main(String[] args) {
        // Set Look and Feel and setup global Thai font
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UITheme.setupGlobalThaiFont();

        // Launch GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            PotionShopGUI gui = new PotionShopGUI();
            gui.setVisible(true);
        });
    }
}
