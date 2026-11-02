package com.potioncraft.ui;

import com.potioncraft.model.potion.Ingredient;
import com.potioncraft.model.potion.Potion;
import com.potioncraft.model.potion.RecipeBook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Grimoire / Recipe Book Dialog showing all available alchemical formulas.
 */
public class RecipeBookDialog extends JDialog {

    public RecipeBookDialog(Frame parent) {
        super(parent, "📖 ตำราปรุงยาศักดิ์สิทธิ์ (Alchemist Grimoire)", true);
        setSize(780, 560);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(24, 26, 32));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("📖 บันทึกสูตรโอสถและวิธีผสม (Potion Recipes)", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(new Color(255, 215, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subLabel = new JLabel("ใส่ส่วนผสมลงในหม้อต้ม (Cauldron) ให้ตรงตามสูตร แล้วกดปุ่ม ปรุงยา (Brew)", SwingConstants.CENTER);
        subLabel.setFont(UITheme.FONT_REGULAR);
        subLabel.setForeground(new Color(180, 190, 205));
        headerPanel.add(subLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Recipe List
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(24, 26, 32));

        List<Potion> recipes = RecipeBook.getAllRecipes();
        for (Potion potion : recipes) {
            listPanel.add(createRecipeRow(potion));
            listPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 80), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(24, 26, 32));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JButton closeBtn = new JButton("ปิดตำรา (Close)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? new Color(75, 85, 105) : new Color(55, 65, 81);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFont(UITheme.FONT_BOLD);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new EmptyBorder(8, 18, 8, 18));
        closeBtn.addActionListener(e -> dispose());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(closeBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createRecipeRow(Potion potion) {
        JPanel card = new JPanel(new BorderLayout(12, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(36, 40, 52));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(potion.getLiquidColor());
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        // Left Icon
        ImageIcon potionArt = PotionSprites.icon(potion, 48);
        JLabel iconLabel = potionArt == null ? new JLabel(potion.getIconSymbol()) : new JLabel(potionArt);
        if (potionArt == null) iconLabel.setFont(UITheme.FONT_EMOJI_LARGE);
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.WEST);

        // Center info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(potion.getThaiName() + " (" + potion.getEnglishName() + ")");
        nameLabel.setFont(UITheme.FONT_TITLE);
        nameLabel.setForeground(potion.getGlowColor());
        infoPanel.add(nameLabel);

        JLabel descLabel = new JLabel(potion.getDescription());
        descLabel.setFont(UITheme.FONT_REGULAR);
        descLabel.setForeground(new Color(170, 180, 195));
        infoPanel.add(descLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Right side: Ingredients needed
        JPanel ingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        ingPanel.setOpaque(false);
        for (Ingredient ing : potion.getRecipe()) {
            JPanel tag = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
            tag.setOpaque(true);
            tag.setBackground(new Color(50, 56, 72));
            tag.setBorder(BorderFactory.createLineBorder(ing.getColor(), 1));

            JLabel tagIcon = new JLabel(ing.getIconSymbol());
            tagIcon.setFont(UITheme.FONT_EMOJI_MED);
            tag.add(tagIcon);

            JLabel tagText = new JLabel(ing.getThaiName());
            tagText.setFont(UITheme.FONT_BOLD);
            tagText.setForeground(Color.WHITE);
            tag.add(tagText);

            ingPanel.add(tag);
        }
        card.add(ingPanel, BorderLayout.EAST);

        return card;
    }
}
