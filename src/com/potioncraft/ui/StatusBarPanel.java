package com.potioncraft.ui;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.game.GameEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Top Status and Navigation Bar displaying player Gold, Reputation Hearts,
 * Combo streak, Shop Day, and quick game action buttons.
 */
public class StatusBarPanel extends JPanel {
    private final GameEngine gameEngine;
    private final JLabel goldLabel;
    private final JLabel reputationLabel;
    private final JLabel comboLabel;
    private final JLabel dayLabel;
    private final JLabel scoreLabel;
    private final JButton pauseBtn;
    private final JButton soundBtn;

    public StatusBarPanel(GameEngine gameEngine, Frame parentFrame) {
        this.gameEngine = gameEngine;

        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setPreferredSize(new Dimension(0, 65));

        // Left: Shop Title & Day
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        leftPanel.setOpaque(false);

        JLabel logo = new JLabel("✨ POTION CRAFT SHOP ✨");
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(new Color(255, 215, 0)); // Gold
        leftPanel.add(logo);

        dayLabel = new JLabel("📅 วันที่ 1");
        dayLabel.setFont(UITheme.FONT_BOLD);
        dayLabel.setForeground(new Color(200, 220, 245));
        leftPanel.add(dayLabel);

        add(leftPanel, BorderLayout.WEST);

        // Center: Stats indicators
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 4));
        centerPanel.setOpaque(false);

        goldLabel = new JLabel("💰 0 G");
        goldLabel.setFont(UITheme.FONT_TITLE);
        goldLabel.setForeground(new Color(255, 223, 70));
        centerPanel.add(goldLabel);

        reputationLabel = new JLabel("❤️❤️❤️❤️❤️");
        reputationLabel.setFont(UITheme.FONT_EMOJI_MED);
        centerPanel.add(reputationLabel);

        comboLabel = new JLabel("🔥 Combo x0");
        comboLabel.setFont(UITheme.FONT_BOLD);
        comboLabel.setForeground(new Color(170, 180, 195));
        centerPanel.add(comboLabel);

        scoreLabel = new JLabel("⭐ คะแนน: 0");
        scoreLabel.setFont(UITheme.FONT_BOLD);
        scoreLabel.setForeground(new Color(130, 215, 255));
        centerPanel.add(scoreLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Right: Control Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightPanel.setOpaque(false);

        JButton recipeBtn = createHeaderButton("📖 ตำราสูตร", new Color(44, 62, 80));
        recipeBtn.addActionListener(e -> {
            RecipeBookDialog dialog = new RecipeBookDialog(parentFrame);
            dialog.setVisible(true);
        });
        rightPanel.add(recipeBtn);

        pauseBtn = createHeaderButton("⏸️ พัก", new Color(60, 72, 90));
        pauseBtn.addActionListener(e -> {
            gameEngine.pause();
            pauseBtn.setText(gameEngine.isPaused() ? "▶️ เล่นต่อ" : "⏸️ พัก");
        });
        rightPanel.add(pauseBtn);

        soundBtn = createHeaderButton("🔊 เสียง", new Color(50, 58, 72));
        soundBtn.addActionListener(e -> {
            boolean current = SoundManager.isSoundEnabled();
            SoundManager.setSoundEnabled(!current);
            soundBtn.setText(!current ? "🔊 เสียง" : "🔇 ปิดเสียง");
        });
        rightPanel.add(soundBtn);

        JButton restartBtn = createHeaderButton("🔄 เริ่มใหม่", new Color(183, 28, 28));
        restartBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                parentFrame,
                "ต้องการเริ่มเกมใหม่ทั้งหมดใช่หรือไม่?",
                "ยืนยันการเริ่มใหม่",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                gameEngine.reset();
                gameEngine.start();
                pauseBtn.setText("⏸️ พัก");
            }
        });
        rightPanel.add(restartBtn);

        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createHeaderButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(c.brighter());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    public void updateStats(int gold, int reputation, int score, int combo, int day) {
        goldLabel.setText(String.format("💰 %,d G", gold));
        scoreLabel.setText(String.format("⭐ คะแนน: %,d", score));
        dayLabel.setText("📅 วันที่ " + day);

        // Build hearts string
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < GameEngine.INITIAL_REPUTATION; i++) {
            if (i < reputation) {
                hearts.append("❤️");
            } else {
                hearts.append("🖤");
            }
        }
        reputationLabel.setText(hearts.toString());

        if (combo > 1) {
            comboLabel.setText(String.format("🔥 Combo x%d (+%d G)", combo, (combo - 1) * 15));
            comboLabel.setForeground(new Color(255, 140, 0));
        } else {
            comboLabel.setText("🔥 Combo x0");
            comboLabel.setForeground(new Color(150, 160, 175));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(18, 22, 30, 240));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(45, 55, 75));
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        g2.dispose();
        super.paintComponent(g);
    }
}
