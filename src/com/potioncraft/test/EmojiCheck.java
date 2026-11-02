package com.potioncraft.test;

import java.awt.Font;

public class EmojiCheck {
    public static void main(String[] args) {
        String[] emojis = {"🔥", "💧", "🌻", "🍄", "✨", "❤️", "⚡", "🛡️", "🌫️", "⚔️", "🦅", "🌌", "🧑‍🌾", "👧", "🪓", "🔨", "🛡️", "🏹", "🪕", "💰", "🧙‍♂️", "⚜️", "🗡️", "🔮", "🐉", "👑", "⏳", "🌌"};
        String[] fonts = {"Segoe UI Emoji", "Segoe UI Symbol", "Tahoma"};
        for (String fName : fonts) {
            Font f = new Font(fName, Font.PLAIN, 14);
            int ok = 0;
            for (String e : emojis) {
                if (f.canDisplayUpTo(e) == -1) ok++;
            }
            System.out.println("Font: " + fName + " supported emojis: " + ok + "/" + emojis.length);
        }
    }
}
