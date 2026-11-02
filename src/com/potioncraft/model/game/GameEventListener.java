package com.potioncraft.model.game;

import com.potioncraft.model.customer.Customer;
import com.potioncraft.model.potion.Potion;

/**
 * Observer interface decoupling the Game Engine logic from GUI presentations.
 */
public interface GameEventListener {
    void onCustomerSpawned(Customer customer);
    void onCustomerServed(Customer customer, int rewardGold, int combo);
    void onCustomerAngryLeft(Customer customer, int remainingReputation);
    void onPotionBrewed(Potion potion);
    void onCauldronUpdated();
    void onStatsUpdated(int gold, int reputation, int score, int combo, int day);
    void onGameOver(int finalScore, int totalServed, int totalAngry);
}
