package com.potioncraft.model.customer;

import com.potioncraft.model.potion.Potion;

/**
 * Represents a Common tier customer (e.g., Novice Adventurer, Villager, Farmer).
 * Characters who are forgiving, patient, and afford standard prices.
 */
public class CommonCustomer extends Customer {

    public CommonCustomer(String name, String title, String avatarEmoji, Potion desiredPotion,
                          String waitingDialogue, String servedDialogue, String angryDialogue) {
        super(name, title, avatarEmoji, CustomerRarity.COMMON, desiredPotion,
              CustomerRarity.COMMON.getDefaultPatienceSeconds(),
              waitingDialogue, servedDialogue, angryDialogue);
    }

    @Override
    public int calculateReward() {
        int base = getRarity().getBaseReward();
        // Friendly tip based on remaining patience
        int tip = (int) Math.round(getPatiencePercentage() * 15 * getRarity().getTipMultiplier());
        return base + tip;
    }

    @Override
    public double getPatienceDecayMultiplier() {
        // Normal decay rate: 1.0
        return 1.0;
    }

    @Override
    public String getSpecialTraitDescription() {
        return "😊 ใจเย็นและอดทนสูง ให้เวลาปรุงยาอย่างสบายๆ";
    }
}
