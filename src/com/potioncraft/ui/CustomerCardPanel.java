package com.potioncraft.ui;

import com.potioncraft.model.customer.Customer;
import com.potioncraft.model.customer.CustomerRarity;
import com.potioncraft.model.game.GameEngine;
import com.potioncraft.model.potion.Ingredient;
import com.potioncraft.model.potion.Potion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Visual Counter Slot Panel representing a customer awaiting service.
 * Displays real-time animated patience gauge, rarity badges, avatar, speech dialogue,
 * recipe ingredients hint, and glowing serve interaction button.
 */
public class CustomerCardPanel extends JPanel {
    private final GameEngine gameEngine;
    private Customer currentCustomer;

    private final JLabel rarityBadge;
    private final JLabel avatarLabel;
    private final JLabel nameLabel;
    private final JLabel dialogueLabel;
    private final JLabel potionIconLabel;
    private final JLabel potionOrderLabel;
    private final JPanel recipeHintPanel;
    private final PatienceBar patienceBar;
    private final JButton serveButton;
    private final JPanel contentPanel;
    private final JLabel emptyLabel;

    public CustomerCardPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.currentCustomer = null;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(270, 400));
        setMinimumSize(new Dimension(240, 360));

        // Empty state label
        emptyLabel = new JLabel("<html><center><font face='Tahoma' size='6'>🪑</font><br><br><font face='Tahoma' color='#ffffff'><b>โต๊ะรับรองว่าง</b></font><br><font face='Tahoma' color='#8899aa'>กำลังรอลูกค้าคนถัดไป...</font></center></html>", SwingConstants.CENTER);
        emptyLabel.setFont(UITheme.FONT_REGULAR);
        emptyLabel.setForeground(new Color(130, 145, 165));

        // Content Panel (when customer is present)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Rarity Header
        rarityBadge = new JLabel("COMMON", SwingConstants.CENTER);
        rarityBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        rarityBadge.setFont(UITheme.FONT_BOLD);
        rarityBadge.setOpaque(true);
        rarityBadge.setBorder(new EmptyBorder(3, 12, 3, 12));

        // 2. Avatar & Name
        avatarLabel = new JLabel("🧑‍🌾", SwingConstants.CENTER);
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setFont(UITheme.FONT_EMOJI_HUGE);

        nameLabel = new JLabel("ชื่อลูกค้า", SwingConstants.CENTER);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setFont(UITheme.FONT_TITLE);
        nameLabel.setForeground(Color.WHITE);

        // 3. Speech Bubble
        dialogueLabel = new JLabel("...", SwingConstants.CENTER);
        dialogueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialogueLabel.setFont(new Font(UITheme.THAI_FONT_FAMILY, Font.ITALIC, 11));
        dialogueLabel.setForeground(new Color(220, 230, 242));
        dialogueLabel.setPreferredSize(new Dimension(230, 40));
        dialogueLabel.setMaximumSize(new Dimension(240, 44));

        // 4. Order Box
        JPanel orderBox = new JPanel();
        orderBox.setLayout(new BoxLayout(orderBox, BoxLayout.Y_AXIS));
        orderBox.setOpaque(true);
        orderBox.setBackground(new Color(20, 23, 30, 200));
        orderBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 80), 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        orderBox.setMaximumSize(new Dimension(240, 75));

        JPanel orderRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        orderRow.setOpaque(false);

        potionIconLabel = new JLabel("❤️");
        potionIconLabel.setFont(UITheme.FONT_EMOJI_MED);
        orderRow.add(potionIconLabel);

        potionOrderLabel = new JLabel("ยาฟื้นพลัง");
        potionOrderLabel.setFont(UITheme.FONT_BOLD);
        orderRow.add(potionOrderLabel);

        orderBox.add(orderRow);
        orderBox.add(Box.createVerticalStrut(4));

        recipeHintPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        recipeHintPanel.setOpaque(false);
        orderBox.add(recipeHintPanel);

        // 5. Patience Bar
        patienceBar = new PatienceBar();
        patienceBar.setMaximumSize(new Dimension(240, 22));

        // 6. Serve Button
        serveButton = new JButton("เสิร์ฟน้ำยา (Serve)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(getBackground().brighter());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        serveButton.setContentAreaFilled(false);
        serveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        serveButton.setFont(UITheme.FONT_BOLD);
        serveButton.setFocusPainted(false);
        serveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        serveButton.setMaximumSize(new Dimension(240, 36));
        serveButton.setPreferredSize(new Dimension(240, 36));
        serveButton.addActionListener(e -> {
            if (currentCustomer != null) {
                gameEngine.serveCustomer(currentCustomer);
            }
        });

        // Assemble Content
        contentPanel.add(rarityBadge);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(avatarLabel);
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(dialogueLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(orderBox);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(patienceBar);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(serveButton);

        add(emptyLabel, BorderLayout.CENTER);
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        removeAll();

        if (customer == null) {
            add(emptyLabel, BorderLayout.CENTER);
        } else {
            CustomerRarity rarity = customer.getRarity();
            rarityBadge.setText(rarity.getTierName().toUpperCase() + " • " + rarity.getThaiName());
            rarityBadge.setBackground(rarity.getPrimaryColor());
            rarityBadge.setForeground(Color.WHITE);

            avatarLabel.setText(customer.getAvatarEmoji());
            nameLabel.setText(customer.getName() + " (" + customer.getTitle() + ")");
            dialogueLabel.setText("<html><center><font face='Tahoma'>\"" + customer.getWaitingDialogue() + "\"</font></center></html>");

            Potion order = customer.getDesiredPotion();
            ImageIcon orderArt = PotionSprites.icon(order, 28);
            potionIconLabel.setIcon(orderArt);
            potionIconLabel.setText(orderArt == null ? order.getIconSymbol() : "");
            potionOrderLabel.setText(order.getThaiName());
            potionOrderLabel.setForeground(order.getGlowColor());

            // Recipe Hint Tags
            recipeHintPanel.removeAll();
            for (Ingredient ing : order.getRecipe()) {
                JLabel tag = new JLabel(ing.getIconSymbol());
                tag.setFont(UITheme.FONT_EMOJI_MED);
                tag.setToolTipText(ing.getThaiName());
                recipeHintPanel.add(tag);
            }
            recipeHintPanel.revalidate();

            add(contentPanel, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    public Customer getCustomer() {
        return currentCustomer;
    }

    /**
     * Called on each game loop tick to update patience visuals & button glow.
     */
    public void updateVisuals() {
        if (currentCustomer != null) {
            patienceBar.setPatience(currentCustomer.getPatiencePercentage(), currentCustomer.getCurrentPatienceSeconds());

            // Check if player holds the matching potion
            Potion held = gameEngine.getCurrentHeldPotion();
            boolean isMatch = (held != null && held.equals(currentCustomer.getDesiredPotion()));

            if (isMatch) {
                serveButton.setText("✨ เสิร์ฟโอสถตรงตามสั่ง! (+Gold)");
                serveButton.setBackground(new Color(46, 125, 50));
                serveButton.setForeground(Color.WHITE);
                serveButton.setEnabled(true);
            } else if (held != null) {
                serveButton.setText("เสิร์ฟ (โอสถไม่ตรง!)");
                serveButton.setBackground(new Color(183, 28, 28));
                serveButton.setForeground(Color.WHITE);
                serveButton.setEnabled(true);
            } else {
                serveButton.setText("รอปรุงโอสถ...");
                serveButton.setBackground(new Color(55, 60, 75));
                serveButton.setForeground(new Color(170, 180, 195));
                serveButton.setEnabled(false);
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (currentCustomer == null) {
            // Empty slot style
            g2.setColor(new Color(30, 34, 44, 180));
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(55, 62, 78));
            Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0);
            g2.setStroke(dashed);
            g2.drawRoundRect(1, 1, w - 2, h - 2, 16, 16);
        } else {
            // Customer card background
            CustomerRarity rarity = currentCustomer.getRarity();
            g2.setColor(new Color(28, 32, 42));
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            // Subtle glowing border corresponding to customer tier
            float strokeWidth = (rarity == CustomerRarity.LEGENDARY) ? 3.0f : 2.0f;
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.setColor(rarity.getPrimaryColor());
            g2.drawRoundRect(1, 1, w - 2, h - 2, 16, 16);
        }
        g2.dispose();
    }

    /**
     * Custom component rendering a smooth dynamic patience gauge.
     */
    private static class PatienceBar extends JComponent {
        private double ratio = 1.0;
        private double remainingSeconds = 30.0;

        public void setPatience(double ratio, double remainingSeconds) {
            this.ratio = Math.max(0.0, Math.min(1.0, ratio));
            this.remainingSeconds = remainingSeconds;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background track
            g2.setColor(new Color(18, 20, 26));
            g2.fillRoundRect(0, 0, w, h, 8, 8);

            // Fill color transition based on patience
            Color startColor;
            Color endColor;
            if (ratio > 0.5) {
                startColor = new Color(76, 175, 80);
                endColor = new Color(139, 195, 74);
            } else if (ratio > 0.25) {
                startColor = new Color(255, 152, 0);
                endColor = new Color(255, 193, 7);
            } else {
                startColor = new Color(211, 47, 47);
                endColor = new Color(255, 82, 82);
            }

            int fillWidth = (int) (w * ratio);
            if (fillWidth > 0) {
                g2.setPaint(new GradientPaint(0, 0, startColor, fillWidth, 0, endColor));
                g2.fillRoundRect(0, 0, fillWidth, h, 8, 8);
            }

            // Outline
            g2.setColor(new Color(55, 62, 78));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

            // Text overlay
            String text = String.format("ความอดทน: %.1f วินาที", remainingSeconds);
            if (ratio <= 0.25) {
                text = String.format("รีบด่วน! เหลือ %.1f วิ", remainingSeconds);
            }
            g2.setFont(new Font(UITheme.THAI_FONT_FAMILY, Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent();

            g2.setColor(Color.WHITE);
            g2.drawString(text, (w - textW) / 2, (h + textH) / 2 - 2);

            g2.dispose();
        }
    }
}
