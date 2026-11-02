package com.potioncraft.ui;

import com.potioncraft.model.game.GameEngine;
import com.potioncraft.model.potion.Ingredient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Apothecary Herb Cabinet component utilizing the pixel art 'herb_box_cutout.png' asset.
 * Features 5 selectable ingredient slots and a quick recipe book button.
 */
public class HerbCabinetPanel extends JPanel {
    private final GameEngine gameEngine;

    public HerbCabinetPanel(GameEngine gameEngine, Frame parentFrame) {
        this.gameEngine = gameEngine;

        setPreferredSize(new Dimension(310, 230));
        setMinimumSize(new Dimension(290, 210));
        setMaximumSize(new Dimension(330, 240));
        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 16, 50, 16)); // Bottom padding leaves the wooden drawers visible

        // Title at the top of the cabinet
        JLabel cabinetTitle = new JLabel("🌿 ตู้สมุนไพร (Apothecary)", SwingConstants.CENTER);
        cabinetTitle.setFont(UITheme.FONT_BOLD);
        cabinetTitle.setForeground(new Color(255, 230, 160));
        add(cabinetTitle, BorderLayout.NORTH);

        // Grid for the 6 shelf compartments (2 rows x 3 columns)
        JPanel grid = new JPanel(new GridLayout(2, 3, 6, 6));
        grid.setOpaque(false);

        // 1. Fire Herb
        grid.add(createIngredientSlotButton(Ingredient.DRAGON_SCALE));
        // 2. Moon Leaf
        grid.add(createIngredientSlotButton(Ingredient.MERMAIDS_TEARDROP));
        // 3. Sun Petal
        grid.add(createIngredientSlotButton(Ingredient.SUNFLOWER_POLLEN));
        // 4. Void Shroom
        grid.add(createIngredientSlotButton(Ingredient.LUMINOUS_SHROOM));
        // 5. Dragon Fern
        grid.add(createIngredientSlotButton(Ingredient.RAINBOW_CRYSTAL));

        // 6. Recipe Book shortcut slot
        JButton recipeBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(50, 65, 85, 230) : new Color(30, 40, 55, 200);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 215, 0));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        recipeBtn.setContentAreaFilled(false);
        recipeBtn.setLayout(new BorderLayout(2, 0));
        recipeBtn.setBorder(new EmptyBorder(3, 4, 3, 4));
        recipeBtn.setFocusPainted(false);
        recipeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        recipeBtn.setToolTipText("เปิดตำราดูสูตรผสมโอสถทั้งหมด");

        JLabel bookIcon = new JLabel("📖", SwingConstants.CENTER);
        bookIcon.setFont(UITheme.FONT_EMOJI_MED);
        recipeBtn.add(bookIcon, BorderLayout.WEST);

        JLabel bookText = new JLabel("<html><center><b>สูตรยา</b><br><font size='2' color='#ffd700'>Recipe</font></center></html>", SwingConstants.CENTER);
        bookText.setFont(UITheme.FONT_SMALL);
        bookText.setForeground(Color.WHITE);
        recipeBtn.add(bookText, BorderLayout.CENTER);

        recipeBtn.addActionListener(e -> {
            RecipeBookDialog dialog = new RecipeBookDialog(parentFrame);
            dialog.setVisible(true);
        });
        grid.add(recipeBtn);

        add(grid, BorderLayout.CENTER);
    }

    private JButton createIngredientSlotButton(Ingredient ing) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glassy dark overlay showing wood through slightly
                Color bg = getModel().isRollover() ? new Color(48, 55, 72, 235) : new Color(24, 28, 38, 205);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ing.getColor());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setLayout(new BorderLayout(4, 0));
        btn.setBorder(new EmptyBorder(3, 4, 3, 4));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("คลิกเพื่อใส่ " + ing.getThaiName() + " ลงในหม้อต้ม");

        JLabel icon = new JLabel(ing.getIconSymbol(), SwingConstants.CENTER);
        icon.setFont(UITheme.FONT_EMOJI_MED);
        btn.add(icon, BorderLayout.WEST);

        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(ing.getThaiName());
        nameLabel.setFont(UITheme.FONT_BOLD);
        nameLabel.setForeground(Color.WHITE);
        labelPanel.add(nameLabel);

        JLabel engLabel = new JLabel(ing.getEnglishName());
        engLabel.setFont(UITheme.FONT_SMALL);
        engLabel.setForeground(new Color(190, 205, 220));
        labelPanel.add(engLabel);

        btn.add(labelPanel, BorderLayout.CENTER);

        btn.addActionListener(e -> gameEngine.addIngredient(ing));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        BufferedImage herbImg = AssetManager.getHerbBox();
        if (herbImg != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            // Draw pixel art herb box cabinet
            g2.drawImage(herbImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Fallback wooden cabinet
            g2.setColor(new Color(75, 45, 25));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
