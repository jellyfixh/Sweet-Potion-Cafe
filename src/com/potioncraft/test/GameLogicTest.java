package com.potioncraft.test;

import com.potioncraft.model.customer.*;
import com.potioncraft.model.potion.*;

import java.util.Arrays;

/**
 * Automated test suite to verify OOP domain models, brewing recipes,
 * customer tier patience, and reward calculations.
 */
public class GameLogicTest {
    public static void main(String[] args) {
        System.out.println("=== Starting Game Logic Tests ===");

        // Test 1: Brewing Recipes
        Cauldron cauldron = new Cauldron();
        cauldron.addIngredient(Ingredient.DRAGON_SCALE);
        cauldron.addIngredient(Ingredient.SUNFLOWER_POLLEN);
        Potion healthPotion = cauldron.brew();
        assertNotNull("Brew result should not be null", healthPotion);
        assertEquals("HEALTH_POTION", healthPotion.getId());
        System.out.println("✔ Test 1 Passed: Successfully brewed Health Potion [Fire Herb + Sun Petal]");

        // Test 2: Order of ingredients does not matter
        cauldron.clear();
        cauldron.addIngredient(Ingredient.SUNFLOWER_POLLEN);
        cauldron.addIngredient(Ingredient.DRAGON_SCALE);
        Potion reversedHealth = cauldron.brew();
        assertEquals("HEALTH_POTION", reversedHealth.getId());
        System.out.println("✔ Test 2 Passed: Ingredient order independence verified");

        // Test 3: Failed brew (Sludge)
        cauldron.clear();
        cauldron.addIngredient(Ingredient.DRAGON_SCALE);
        cauldron.addIngredient(Ingredient.RAINBOW_CRYSTAL); // without sun petal, not phoenix
        Potion sludge = cauldron.brew();
        assertTrue("Sludge should be marked as failed", sludge.isFailed());
        System.out.println("✔ Test 3 Passed: Invalid recipe results in Sludge");

        // Test 4: Customer Tiers & Polymorphic Rewards
        CommonCustomer common = new CommonCustomer("Test Common", "Novice", "🧑‍🌾", RecipeBook.HEALTH_POTION, "Wait", "Thanks", "Grr");
        RareCustomer rare = new RareCustomer("Test Rare", "Knight", "🛡️", RecipeBook.MANA_POTION, "Wait", "Thanks", "Grr");
        EpicCustomer epic = new EpicCustomer("Test Epic", "Mage", "🧙‍♂️", RecipeBook.BERSERK_POTION, "Wait", "Thanks", "Grr");
        LegendaryCustomer legendary = new LegendaryCustomer("Test Leg", "Dragon", "🐉", RecipeBook.PHOENIX_ELIXIR, "Wait", "Thanks", "Grr");

        // Verify tier attributes
        assertTrue("Common patience > Rare patience", common.getMaxPatienceSeconds() > rare.getMaxPatienceSeconds());
        assertTrue("Rare patience > Epic patience", rare.getMaxPatienceSeconds() > epic.getMaxPatienceSeconds());
        assertTrue("Epic patience > Legendary patience", epic.getMaxPatienceSeconds() > legendary.getMaxPatienceSeconds());

        // Verify rewards hierarchy
        int commonReward = common.calculateReward();
        int rareReward = rare.calculateReward();
        int epicReward = epic.calculateReward();
        int legendaryReward = legendary.calculateReward();

        System.out.printf("Rewards at 100%% patience: Common=%dG, Rare=%dG, Epic=%dG, Legendary=%dG%n",
            commonReward, rareReward, epicReward, legendaryReward);

        assertTrue("Rare reward > Common reward", rareReward > commonReward);
        assertTrue("Epic reward > Rare reward", epicReward > rareReward);
        assertTrue("Legendary reward > Epic reward", legendaryReward > epicReward);
        System.out.println("✔ Test 4 Passed: Customer Tier Patience and Polymorphic Rewards hierarchy verified");

        // Test 5: Customer Patience Decay
        double initialPatience = epic.getCurrentPatienceSeconds();
        epic.updatePatience(2.0); // 2 seconds
        double decayedPatience = epic.getCurrentPatienceSeconds();
        assertTrue("Patience should have decayed", decayedPatience < initialPatience);
        System.out.println("✔ Test 5 Passed: Customer patience decay working properly");

        System.out.println("\n🎉 ALL UNIT TESTS PASSED SUCCESSFULLY! 🎉");
    }

    private static void assertNotNull(String msg, Object obj) {
        if (obj == null) throw new AssertionError(msg);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected [" + expected + "] but got [" + actual + "]");
        }
    }

    private static void assertTrue(String msg, boolean condition) {
        if (!condition) throw new AssertionError(msg);
    }
}
