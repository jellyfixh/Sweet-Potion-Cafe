package com.potioncraft.model.potion;

import java.awt.Color;

/**
 * Represents raw ingredients that can be gathered and brewed into potions.
 */
public enum Ingredient {
    DRAGON_SCALE("Dragon Scale", "เกล็ดมังกร", "เกล็ดมังกรสีแดงเพลิง แวววาวและเปี่ยมด้วยพลัง", "◆", new Color(220, 66, 66)),
    MERMAIDS_TEARDROP("Mermaid's Teardrop", "หยดน้ำตานางเงือก", "หยาดน้ำเทอร์ควอยซ์เรืองแสงในเปลือกหอยมุก", "◊", new Color(70, 218, 218)),
    SUNFLOWER_POLLEN("Sunflower Pollen", "เกสรดอกสุริยัน", "เกสรสีทองอมส้ม อบอุ่นด้วยประกายแสงอาทิตย์", "☀", new Color(255, 190, 64)),
    LUMINOUS_SHROOM("Luminous Shroom", "เห็ดเรืองแสง", "เห็ดหมวกม่วงน้ำเงิน แต้มจุดเรืองแสงสีฟ้า", "●", new Color(120, 110, 230)),
    FROST_PETAL("Frost Petal", "กลีบดอกเหมันต์", "กลีบดอกสีฟ้าหิมะ เคลือบเกล็ดน้ำแข็งใส", "*", new Color(150, 220, 250)),
    MOONLIGHT_DUST("Moonlight Dust", "ละอองแสงจันทร์", "ละอองดาวสีเงินม่วงอ่อน บรรจุในขวดแก้วเวทมนตร์", "+", new Color(195, 175, 240)),
    MILLENNIAL_ROOT("Millennial Root", "รากไม้พันปี", "รากไม้โบราณขดเป็นเกลียว มีมอสและรูนเรืองแสง", "~", new Color(170, 126, 80)),
    RAINBOW_CRYSTAL("Rainbow Crystal", "คริสตัลสีรุ้ง", "ผลึกคริสตัลสะท้อนแสงสีรุ้งพาสเทล", "◇", new Color(210, 160, 235));
    private final String englishName;
    private final String thaiName;
    private final String description;
    private final String iconSymbol;
    private final Color color;

    Ingredient(String englishName, String thaiName, String description, String iconSymbol, Color color) {
        this.englishName = englishName;
        this.thaiName = thaiName;
        this.description = description;
        this.iconSymbol = iconSymbol;
        this.color = color;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getThaiName() {
        return thaiName;
    }

    public String getDescription() {
        return description;
    }

    public String getIconSymbol() {
        return iconSymbol;
    }

    public Color getColor() {
        return color;
    }

    public String getDisplayName() {
        return iconSymbol + " " + thaiName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
