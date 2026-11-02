package com.potioncraft.model.potion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a crafted potion or concoction in the game.
 */
public class Potion {
    private final String id;
    private final String englishName;
    private final String thaiName;
    private final String description;
    private final String iconSymbol;
    private final Color liquidColor;
    private final Color glowColor;
    private final List<Ingredient> recipe;
    private final boolean isFailed;

    public Potion(String id, String englishName, String thaiName, String description,
                  String iconSymbol, Color liquidColor, Color glowColor, List<Ingredient> recipe) {
        this(id, englishName, thaiName, description, iconSymbol, liquidColor, glowColor, recipe, false);
    }

    public Potion(String id, String englishName, String thaiName, String description,
                  String iconSymbol, Color liquidColor, Color glowColor, List<Ingredient> recipe, boolean isFailed) {
        this.id = id;
        this.englishName = englishName;
        this.thaiName = thaiName;
        this.description = description;
        this.iconSymbol = iconSymbol;
        this.liquidColor = liquidColor;
        this.glowColor = glowColor;
        this.recipe = new ArrayList<>(recipe != null ? recipe : Collections.emptyList());
        this.isFailed = isFailed;
    }

    /**
     * Factory method for creating a failed brew (โคลนเน่าเสีย)
     */
    public static Potion createFailedSludge(List<Ingredient> attemptedIngredients) {
        return new Potion(
            "SLUDGE",
            "Murky Sludge",
            "โคลนเน่าไร้ค่า",
            "ส่วนผสมไม่เข้ากัน กลายเป็นโคลนสีเทากลิ่นเหม็นหึ่ง!",
            "💩",
            new Color(90, 80, 75),
            new Color(60, 50, 45),
            attemptedIngredients,
            true
        );
    }

    public String getId() {
        return id;
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

    public Color getLiquidColor() {
        return liquidColor;
    }

    public Color getGlowColor() {
        return glowColor;
    }

    public List<Ingredient> getRecipe() {
        return Collections.unmodifiableList(recipe);
    }

    public boolean isFailed() {
        return isFailed;
    }

    public String getRecipeSummary() {
        if (recipe.isEmpty()) return "ไม่มีส่วนผสม";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recipe.size(); i++) {
            sb.append(recipe.get(i).getDisplayName());
            if (i < recipe.size() - 1) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Potion potion = (Potion) o;
        return isFailed == potion.isFailed && Objects.equals(id, potion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isFailed);
    }

    @Override
    public String toString() {
        return iconSymbol + " " + thaiName;
    }
}
