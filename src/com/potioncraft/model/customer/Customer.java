package com.potioncraft.model.customer;

import com.potioncraft.model.potion.Potion;
import java.util.UUID;

/**
 * Abstract Base Class representing a Customer visiting the Potion Shop.
 * Demonstrates Object-Oriented principles:
 * - Abstraction: Abstract base template for all customer tiers.
 * - Encapsulation: Private state with guarded accessors and behaviors.
 * - Polymorphism: Derived classes customize reward calculation and patience decay.
 */
public abstract class Customer {
    private final String id;
    private final String name;
    private final String title;
    private final String avatarEmoji;
    private final CustomerRarity rarity;
    private final Potion desiredPotion;
    private final double maxPatienceSeconds;
    private double currentPatienceSeconds;
    private final String waitingDialogue;
    private final String servedDialogue;
    private final String angryDialogue;
    private boolean served;
    private boolean leftAngry;
    private CustomerProfile profile;

    public Customer(String name, String title, String avatarEmoji, CustomerRarity rarity,
                    Potion desiredPotion, double maxPatienceSeconds,
                    String waitingDialogue, String servedDialogue, String angryDialogue) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.title = title;
        this.avatarEmoji = avatarEmoji;
        this.rarity = rarity;
        this.desiredPotion = desiredPotion;
        this.maxPatienceSeconds = maxPatienceSeconds > 0 ? maxPatienceSeconds : rarity.getDefaultPatienceSeconds();
        this.currentPatienceSeconds = this.maxPatienceSeconds;
        this.waitingDialogue = waitingDialogue;
        this.servedDialogue = servedDialogue;
        this.angryDialogue = angryDialogue;
        this.served = false;
        this.leftAngry = false;
    }

    /**
     * Decreases the customer's patience according to elapsed delta time and decay multiplier.
     * @param deltaSeconds elapsed time in seconds
     */
    public void updatePatience(double deltaSeconds) {
        if (served || leftAngry) return;

        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0) return;
        double decay = deltaSeconds * getPatienceDecayMultiplier()
                * (profile == null ? 1 : profile.decay(getPatiencePercentage()));
        currentPatienceSeconds -= decay;
        if (currentPatienceSeconds <= 0.0) {
            currentPatienceSeconds = 0.0;
            leftAngry = true;
        }
    }

    /**
     * Polymorphic method: Calculates the gold payout for satisfying this customer.
     * Takes remaining patience ratio into account for tip calculation.
     * @return Total gold earned
     */
    public abstract int calculateReward();

    /**
     * Polymorphic method: Specifies how fast this customer's patience decays.
     * Higher-tier customers get impatient faster!
     * @return multiplier (e.g. 1.0, 1.2, 1.5)
     */
    public abstract double getPatienceDecayMultiplier();

    /**
     * Returns a summary of special traits/abilities of this customer tier.
     */
    public abstract String getSpecialTraitDescription();

    void setProfile(CustomerProfile profile) { this.profile = profile; }
    public CustomerProfile getProfile() { return profile; }
    /** Angry expression starts before the customer gives up and leaves. */
    public CustomerMood getMood() { return CustomerMood.fromPatience(getPatiencePercentage()); }
    public String getPersonality() { return profile == null ? getSpecialTraitDescription() : profile.getTrait(); }
    public int getPersonalityReward() {
        return (int)Math.round(calculateReward() * (profile == null ? 1 : profile.reward(getPatiencePercentage())));
    }
    public void onBrewFailed() { if (profile != null) updatePatience(profile.getMistakePenalty()); }

    // State checking methods
    public boolean isAngry() {
        return leftAngry || currentPatienceSeconds <= 0.0;
    }

    public boolean isServed() {
        return served;
    }

    public void markServed() {
        this.served = true;
    }

    public double getPatiencePercentage() {
        if (maxPatienceSeconds <= 0) return 0.0;
        return Math.max(0.0, Math.min(1.0, currentPatienceSeconds / maxPatienceSeconds));
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public CustomerRarity getRarity() {
        return rarity;
    }

    public Potion getDesiredPotion() {
        return desiredPotion;
    }

    public double getMaxPatienceSeconds() {
        return maxPatienceSeconds;
    }

    public double getCurrentPatienceSeconds() {
        return currentPatienceSeconds;
    }

    public String getWaitingDialogue() {
        return waitingDialogue;
    }

    public String getServedDialogue() {
        return servedDialogue;
    }

    public String getAngryDialogue() {
        return angryDialogue;
    }

    public String getFullNameWithTitle() {
        return avatarEmoji + " " + name + " (" + title + ")";
    }

    @Override
    public String toString() {
        return "[" + rarity.getTierName() + "] " + name + " -> อยากได้: " + desiredPotion.getThaiName();
    }
}
