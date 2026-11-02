package com.potioncraft.ui;

import com.potioncraft.model.game.GameEngine;
import com.potioncraft.model.potion.Cauldron;
import com.potioncraft.model.potion.Ingredient;
import com.potioncraft.model.potion.Potion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Interactive Crafting Station Panel utilizing:
 * - 'counter_layer.png' as the wooden workbench counter background
 * - 'HerbCabinetPanel' (herb_box_cutout.png) as the apothecary ingredient cabinet
 * - Animated Bubbling Cauldron with active ingredients slots
 * - Brewing and Dump controls
 * - Serving Tray showing the currently held Potion ready for customer delivery
 */
public class CauldronPanel extends JPanel {
    private final GameEngine gameEngine;

    // Visual sub-components
    private final HerbCabinetPanel herbCabinet;
    private final CauldronView cauldronView;
    private final JPanel slotsPanel;
    private final JButton brewButton;
    private final JButton clearButton;
    private final ServingTrayPanel servingTrayPanel;

    public CauldronPanel(GameEngine gameEngine, Frame parentFrame) {
        this.gameEngine = gameEngine;
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(0, 240));
        setBorder(new EmptyBorder(8, 15, 8, 15));

        // 1. WEST: Apothecary Herb Cabinet (using herb_box_cutout.png)
        herbCabinet = new HerbCabinetPanel(gameEngine, parentFrame);
        add(herbCabinet, BorderLayout.WEST);

        // 2. CENTER: The Cauldron brewing area (placed on the red table runner)
        JPanel centerArea = new JPanel(new BorderLayout(0, 6));
        centerArea.setOpaque(false);

        cauldronView = new CauldronView();
        centerArea.add(cauldronView, BorderLayout.CENTER);

        // Control buttons and ingredient slots
        JPanel cauldronControls = new JPanel();
        cauldronControls.setLayout(new BoxLayout(cauldronControls, BoxLayout.Y_AXIS));
        cauldronControls.setOpaque(false);

        JLabel slotsTitle = new JLabel("ส่วนผสมในหม้อต้ม (สูงสุด 3 ชนิด):", SwingConstants.CENTER);
        slotsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        slotsTitle.setFont(UITheme.FONT_BOLD);
        slotsTitle.setForeground(new Color(255, 240, 200));
        cauldronControls.add(slotsTitle);
        cauldronControls.add(Box.createVerticalStrut(4));

        slotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        slotsPanel.setOpaque(false);
        cauldronControls.add(slotsPanel);
        cauldronControls.add(Box.createVerticalStrut(6));

        // Action Buttons
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionButtonsPanel.setOpaque(false);

        brewButton = createCustomButton("✨ ปรุงโอสถ (BREW)", new Color(106, 27, 154), new Color(156, 39, 176));
        brewButton.setPreferredSize(new Dimension(185, 38));
        brewButton.addActionListener(e -> gameEngine.brew());
        actionButtonsPanel.add(brewButton);

        clearButton = createCustomButton("ล้างหม้อ (Clear)", new Color(55, 62, 78), new Color(85, 95, 115));
        clearButton.setPreferredSize(new Dimension(140, 38));
        clearButton.addActionListener(e -> gameEngine.clearCauldron());
        actionButtonsPanel.add(clearButton);

        cauldronControls.add(actionButtonsPanel);
        centerArea.add(cauldronControls, BorderLayout.SOUTH);

        add(centerArea, BorderLayout.CENTER);

        // 3. EAST: Serving Tray (Held Potion)
        servingTrayPanel = new ServingTrayPanel();
        add(servingTrayPanel, BorderLayout.EAST);

        updateCauldronDisplay();
    }

    private JButton createCustomButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = isEnabled() ? (getModel().isRollover() ? hoverColor : baseColor) : new Color(45, 50, 62);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(c.brighter());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateCauldronDisplay() {
        Cauldron cauldron = gameEngine.getCauldron();
        List<Ingredient> ingredients = cauldron.getCurrentIngredients();

        // Update slots
        slotsPanel.removeAll();
        for (int i = 0; i < Cauldron.MAX_INGREDIENTS; i++) {
            if (i < ingredients.size()) {
                Ingredient ing = ingredients.get(i);
                JPanel slotCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
                slotCard.setOpaque(true);
                slotCard.setBackground(new Color(30, 36, 48, 230));
                slotCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ing.getColor(), 2),
                    new EmptyBorder(3, 8, 3, 8)
                ));

                JLabel icon = new JLabel(ing.getIconSymbol());
                icon.setFont(UITheme.FONT_EMOJI_MED);
                slotCard.add(icon);

                JLabel name = new JLabel(ing.getThaiName());
                name.setFont(UITheme.FONT_BOLD);
                name.setForeground(Color.WHITE);
                slotCard.add(name);

                slotsPanel.add(slotCard);
            } else {
                JLabel emptySlot = new JLabel("ว่าง", SwingConstants.CENTER);
                emptySlot.setFont(UITheme.FONT_REGULAR);
                emptySlot.setOpaque(true);
                emptySlot.setBackground(new Color(22, 25, 32, 200));
                emptySlot.setForeground(new Color(130, 140, 155));
                emptySlot.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 62, 75), 1),
                    new EmptyBorder(4, 14, 4, 14)
                ));
                slotsPanel.add(emptySlot);
            }
        }

        brewButton.setEnabled(!cauldron.isEmpty());
        clearButton.setEnabled(!cauldron.isEmpty());

        servingTrayPanel.updateHeldPotion(gameEngine.getCurrentHeldPotion());

        slotsPanel.revalidate();
        slotsPanel.repaint();
        cauldronView.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        BufferedImage counter = AssetManager.getCounterLayer();
        if (counter != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            // Draw the counter table portion (Y: 535 to 768) scaled to fit this workbench panel
            int srcY = 535;
            int srcH = counter.getHeight() - srcY;
            g2.drawImage(counter, 0, 0, getWidth(), getHeight(), 0, srcY, counter.getWidth(), counter.getHeight(), null);

            // Subtle dark wash for readability
            g2.setColor(new Color(10, 12, 16, 70));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fallback dark wood
            g2.setColor(new Color(36, 26, 20));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Custom animated Cauldron rendering component with rising bubbles.
     */
    private class CauldronView extends JComponent {
        private final List<Bubble> bubbles = new ArrayList<>();
        private final Random random = new Random();

        public CauldronView() {
            setPreferredSize(new Dimension(280, 100));
            Timer animTimer = new Timer(50, e -> {
                if (isShowing()) {
                    updateBubbles();
                    repaint();
                }
            });
            animTimer.start();
        }

        private void updateBubbles() {
            if (random.nextInt(3) == 0 && bubbles.size() < 12) {
                bubbles.add(new Bubble(
                    getWidth() / 2 - 35 + random.nextInt(70),
                    getHeight() - 22,
                    4 + random.nextInt(8),
                    1.5 + random.nextDouble() * 2.0
                ));
            }

            for (int i = bubbles.size() - 1; i >= 0; i--) {
                Bubble b = bubbles.get(i);
                b.y -= b.speed;
                b.wobble += 0.2;
                b.x += Math.sin(b.wobble) * 0.8;
                if (b.y < 8) {
                    bubbles.remove(i);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2 + 8;

            // Liquid color based on ingredients inside
            Color liquidColor = new Color(50, 40, 75);
            List<Ingredient> ingredients = gameEngine.getCauldron().getCurrentIngredients();
            if (!ingredients.isEmpty()) {
                Ingredient last = ingredients.get(ingredients.size() - 1);
                liquidColor = last.getColor();
            }

            // Shadow under cauldron on the wooden counter
            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillOval(cx - 70, cy + 22, 140, 24);

            // Cauldron Body (Cast iron)
            g2.setColor(new Color(25, 27, 34));
            g2.fillOval(cx - 72, cy - 32, 144, 70);

            // Cauldron Rim
            g2.setColor(new Color(45, 49, 60));
            g2.fillOval(cx - 67, cy - 35, 134, 30);

            // Cauldron Liquid Glow
            g2.setPaint(new RadialGradientPaint(
                cx, cy - 20, 52,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{liquidColor.brighter(), liquidColor, liquidColor.darker().darker()}
            ));
            g2.fillOval(cx - 58, cy - 31, 116, 23);

            // Animated bubbles rising from the potion
            for (Bubble b : bubbles) {
                g2.setColor(new Color(liquidColor.getRed(), liquidColor.getGreen(), liquidColor.getBlue(), 170));
                g2.fillOval((int) b.x, (int) b.y, b.size, b.size);
                g2.setColor(Color.WHITE);
                g2.drawOval((int) b.x, (int) b.y, b.size, b.size);
            }

            // Feet & Handles
            g2.setColor(new Color(35, 38, 48));
            g2.fillRect(cx - 52, cy + 23, 14, 16);
            g2.fillRect(cx + 38, cy + 23, 14, 16);

            // Handle rings
            g2.drawOval(cx - 82, cy - 22, 16, 24);
            g2.drawOval(cx + 66, cy - 22, 16, 24);

            g2.dispose();
        }

        private class Bubble {
            double x;
            double y;
            int size;
            double speed;
            double wobble;

            Bubble(double x, double y, int size, double speed) {
                this.x = x;
                this.y = y;
                this.size = size;
                this.speed = speed;
                this.wobble = Math.random() * Math.PI;
            }
        }
    }

    /**
     * Panel representing the Serving Tray showing the ready potion in hand.
     */
    private class ServingTrayPanel extends JPanel {
        private final JLabel potionIcon;
        private final JLabel potionName;
        private final JLabel recipeLabel;
        private final JButton discardButton;

        public ServingTrayPanel() {
            setPreferredSize(new Dimension(280, 210));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 140, 90, 160), 2),
                new EmptyBorder(8, 12, 8, 12)
            ));

            JLabel title = new JLabel("ถาดเสิร์ฟโอสถ (ขวดในมือ)", SwingConstants.CENTER);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setFont(UITheme.FONT_TITLE);
            title.setForeground(new Color(255, 215, 0));
            add(title);
            add(Box.createVerticalStrut(4));

            potionIcon = new JLabel("⭕", SwingConstants.CENTER);
            potionIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            potionIcon.setFont(UITheme.FONT_EMOJI_LARGE);
            add(potionIcon);

            potionName = new JLabel("ยังไม่มีโอสถในมือ", SwingConstants.CENTER);
            potionName.setAlignmentX(Component.CENTER_ALIGNMENT);
            potionName.setFont(UITheme.FONT_TITLE);
            potionName.setForeground(new Color(210, 220, 235));
            add(potionName);

            recipeLabel = new JLabel("ผสมวัตถุดิบแล้วกด ปรุงยา (Brew)", SwingConstants.CENTER);
            recipeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            recipeLabel.setFont(UITheme.FONT_REGULAR);
            recipeLabel.setForeground(new Color(160, 175, 195));
            add(recipeLabel);
            add(Box.createVerticalStrut(8));

            discardButton = new JButton("เททิ้ง (Discard)") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color c = isEnabled() ? new Color(183, 28, 28) : new Color(50, 55, 65);
                    g2.setColor(c);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            discardButton.setContentAreaFilled(false);
            discardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            discardButton.setFont(UITheme.FONT_BOLD);
            discardButton.setForeground(Color.WHITE);
            discardButton.setFocusPainted(false);
            discardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            discardButton.setEnabled(false);
            discardButton.addActionListener(e -> gameEngine.trashHeldPotion());
            add(discardButton);
        }

        public void updateHeldPotion(Potion potion) {
            if (potion == null) {
                potionIcon.setIcon(null);
                potionIcon.setText("⭕");
                potionName.setText("ยังไม่มีโอสถในมือ");
                potionName.setForeground(new Color(180, 190, 205));
                recipeLabel.setText("ผสมวัตถุดิบแล้วกด ปรุงยา (Brew)");
                discardButton.setEnabled(false);
            } else {
                ImageIcon potionArt = PotionSprites.icon(potion, 48);
                potionIcon.setIcon(potionArt);
                potionIcon.setText(potionArt == null ? potion.getIconSymbol() : "");
                potionName.setText(potion.getThaiName());
                potionName.setForeground(potion.getGlowColor());
                recipeLabel.setText(potion.isFailed() ? "ปรุงผิดสูตร! ไม่ตรงกับตำรา" : "สูตร: " + potion.getRecipeSummary());
                discardButton.setEnabled(true);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Rich dark mahogany wood coaster style
            g2.setColor(new Color(25, 20, 18, 215));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
