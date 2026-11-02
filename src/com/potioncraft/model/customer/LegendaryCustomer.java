package com.potioncraft.model.customer;

import com.potioncraft.model.potion.Potion;

/**
 * Represents a Legendary tier customer (e.g., Dragon Overlord, Grand Archmage, Ancient Monarch).
 * Extremely fleeting patience, terrifying presence, but bestows colossal wealth and glory.
 */
public class LegendaryCustomer extends Customer {

    public LegendaryCustomer(String name, String title, String avatarEmoji, Potion desiredPotion,
                             String waitingDialogue, String servedDialogue, String angryDialogue) {
        super(name, title, avatarEmoji, CustomerRarity.LEGENDARY, desiredPotion,
              CustomerRarity.LEGENDARY.getDefaultPatienceSeconds(),
              waitingDialogue, servedDialogue, angryDialogue);
    }

    @Override
    public int calculateReward() {
        int base = getRarity().getBaseReward();
        double patienceRatio = getPatiencePercentage();
        int tip = (int) Math.round(patienceRatio * 150 * getRarity().getTipMultiplier());
        
        // Massive imperial jackpot if served quickly!
        if (patienceRatio >= 0.40) {
            tip += 150;
        }
        return base + tip;
    }

    @Override
    public double getPatienceDecayMultiplier() {
        // High urgency decay rate
        return 1.4;
    }

    @Override
    public String getSpecialTraitDescription() {
        return "👑 ตัวตนในตำนาน! เวลาน้อยยิ่งนัก แต่ผลตอบแทนมหาศาลเปลี่ยนชะตาร้านได้ทันที!";
    }
}
