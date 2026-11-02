package com.potioncraft.model.customer;

import com.potioncraft.model.potion.Potion;

/**
 * Represents an Epic tier customer (e.g., Royal Sorcerer, Arch-Paladin, Noble Alchemist).
 * Very demanding and impatient, but pays generous rewards and a swiftness bonus.
 */
public class EpicCustomer extends Customer {

    public EpicCustomer(String name, String title, String avatarEmoji, Potion desiredPotion,
                        String waitingDialogue, String servedDialogue, String angryDialogue) {
        super(name, title, avatarEmoji, CustomerRarity.EPIC, desiredPotion,
              CustomerRarity.EPIC.getDefaultPatienceSeconds(),
              waitingDialogue, servedDialogue, angryDialogue);
    }

    @Override
    public int calculateReward() {
        int base = getRarity().getBaseReward();
        double patienceRatio = getPatiencePercentage();
        int tip = (int) Math.round(patienceRatio * 70 * getRarity().getTipMultiplier());
        
        // Royal swiftness bonus if served while having more than 50% patience!
        if (patienceRatio >= 0.50) {
            tip += 50;
        }
        return base + tip;
    }

    @Override
    public double getPatienceDecayMultiplier() {
        // Impatient decay rate
        return 1.25;
    }

    @Override
    public String getSpecialTraitDescription() {
        return "🔮 ขุนนางผู้ทรงเกียรติ มอบทิปพิเศษมหาศาลหากเสิร์ฟไวเกิน 50% ของเวลา!";
    }
}
