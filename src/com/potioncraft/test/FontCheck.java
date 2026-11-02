package com.potioncraft.test;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontCheck {
    public static void main(String[] args) {
        String testThai = "ร้านน้ำยาวิเศษ ทดสอบภาษาไทย 123";
        String[] candidates = {"Tahoma", "Leelawadee UI", "Leelawadee", "Microsoft Sans Serif", "Segoe UI", "SansSerif", "Dialog"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        for (String c : candidates) {
            Font f = new Font(c, Font.PLAIN, 14);
            boolean canDisplay = f.canDisplayUpTo(testThai) == -1;
            System.out.println("Font: " + c + " -> canDisplay Thai: " + canDisplay + " (up to: " + f.canDisplayUpTo(testThai) + ")");
        }
    }
}
