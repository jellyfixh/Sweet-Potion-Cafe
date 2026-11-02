package com.potioncraft.model.customer;

import com.potioncraft.model.potion.Potion;

/**
 * Represents a Rare tier customer (e.g., Guild Knight, Traveling Merchant, Ranger).
 * Demands moderate speed and pays handsome rewards for good potions.
 */
public class RareCustomer extends Customer {

    public RareCustomer(String name, String title, String avatarEmoji, Potion desiredPotion,
                        String waitingDialogue, String servedDialogue, String angryDialogue) {
        super(name, title, avatarEmoji, CustomerRarity.RARE, desiredPotion,
              CustomerRarity.RARE.getDefaultPatienceSeconds(),
              waitingDialogue, servedDialogue, angryDialogue);
    }

    @Override
    public int calculateReward() {
        int base = getRarity().getBaseReward();
        int tip = (int) Math.round(getPatiencePercentage() * 35 * getRarity().getTipMultiplier());
        return base + tip;
    }

    @Override
    public double getPatienceDecayMultiplier() {
        // Slightly faster decay due to busy schedule
        return 1.1;
    }

    @Override
    public String getSpecialTraitDescription() {
        return "⚔️ มืออาชีพผู้รีบไปทำภารกิจ ให้เงินรางวัลคุ้มค่า";
    }
}
