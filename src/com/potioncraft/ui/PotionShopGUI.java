package com.potioncraft.ui;
import com.potioncraft.model.game.GameEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Native desktop window; Java Swing and Java2D only. */
public class PotionShopGUI extends JFrame {
    private final PotionGamePanel panel;
    public PotionShopGUI(){
        super("Sweet Potion cafe");
        panel=new PotionGamePanel(new GameEngine());setContentPane(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);setMinimumSize(new Dimension(960,690));
        pack();setLocationRelativeTo(null);
        addWindowFocusListener(new WindowAdapter(){@Override public void windowLostFocus(WindowEvent e){panel.pauseForFocusLoss();}});
        addWindowListener(new WindowAdapter(){@Override public void windowClosed(WindowEvent e){panel.shutdown();}});
    }
}
