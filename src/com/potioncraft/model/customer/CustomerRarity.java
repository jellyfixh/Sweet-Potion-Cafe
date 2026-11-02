package com.potioncraft.model.customer;

import java.awt.Color;

/**
 * Enum representing the 4 tiers of customers in the Potion Shop.
 * Encapsulates tier-specific properties such as patience, base rewards,
 * tip multipliers, and UI theme colors.
 */
public enum CustomerRarity {
    COMMON(
        "Common", 
        "ลูกค้าทั่วไป", 
        30.0, 
        30, 
        1.0, 
        0.50,
        new Color(76, 175, 80),       // Material Green
        new Color(200, 230, 201),
        new Color(46, 125, 50)
    ),
    RARE(
        "Rare", 
        "ลูกค้าหายาก", 
        22.0, 
        75, 
        1.5, 
        0.30,
        new Color(33, 150, 243),      // Material Blue
        new Color(187, 222, 251),
        new Color(21, 101, 192)
    ),
    EPIC(
        "Epic", 
        "ลูกค้าระดับมหากาพย์", 
        15.0, 
        160, 
        2.2, 
        0.15,
        new Color(156, 39, 176),      // Material Purple
        new Color(225, 190, 231),
        new Color(106, 27, 154)
    ),
    LEGENDARY(
        "Legendary", 
        "ลูกค้าระดับตำนาน", 
        10.0, 
        380, 
        3.5, 
        0.05,
        new Color(255, 160, 0),       // Radiant Gold/Amber
        new Color(255, 236, 179),
        new Color(255, 111, 0)
    );

    private final String tierName;
    private final String thaiName;
    private final double defaultPatienceSeconds;
    private final int baseReward;
    private final double tipMultiplier;
    private final double spawnChance; // Weight for probability
    private final Color primaryColor;
    private final Color lightColor;
    private final Color darkColor;

    CustomerRarity(String tierName, String thaiName, double defaultPatienceSeconds, 
                   int baseReward, double tipMultiplier, double spawnChance,
                   Color primaryColor, Color lightColor, Color darkColor) {
        this.tierName = tierName;
        this.thaiName = thaiName;
        this.defaultPatienceSeconds = defaultPatienceSeconds;
        this.baseReward = baseReward;
        this.tipMultiplier = tipMultiplier;
        this.spawnChance = spawnChance;
        this.primaryColor = primaryColor;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public String getTierName() {
        return tierName;
    }

    public String getThaiName() {
        return thaiName;
    }

    public double getDefaultPatienceSeconds() {
        return defaultPatienceSeconds;
    }

    public int getBaseReward() {
        return baseReward;
    }

    public double getTipMultiplier() {
        return tipMultiplier;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getLightColor() {
        return lightColor;
    }

    public Color getDarkColor() {
        return darkColor;
    }

    @Override
    public String toString() {
        return tierName + " (" + thaiName + ")";
    }
}
