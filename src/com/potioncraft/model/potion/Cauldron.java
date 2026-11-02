package com.potioncraft.model.potion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the magical cauldron where the player adds ingredients and brews potions.
 * Encapsulates ingredient capacity and crafting operations.
 */
public class Cauldron {
    public static final int MAX_INGREDIENTS = 3;

    private final List<Ingredient> currentIngredients;

    public Cauldron() {
        this.currentIngredients = new ArrayList<>();
    }

    /**
     * Adds an ingredient into the cauldron if not full.
     * @param ingredient the ingredient to add
     * @return true if added, false if cauldron is full or ingredient is null
     */
    public boolean addIngredient(Ingredient ingredient) {
        if (ingredient == null) return false;
        if (currentIngredients.size() >= MAX_INGREDIENTS) {
            return false;
        }
        currentIngredients.add(ingredient);
        return true;
    }

    /**
     * Removes an ingredient at the specified index.
     */
    public boolean removeIngredient(int index) {
        if (index >= 0 && index < currentIngredients.size()) {
            currentIngredients.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Clears all ingredients from the cauldron (dumps it out).
     */
    public void clear() {
        currentIngredients.clear();
    }

    /**
     * Brews the current ingredients into a Potion and empties the cauldron.
     * @return The brewed Potion (or sludge if recipe does not match)
     */
    public Potion brew() {
        if (currentIngredients.isEmpty()) {
            return null;
        }
        Potion result = RecipeBook.brew(currentIngredients);
        currentIngredients.clear();
        return result;
    }

    public List<Ingredient> getCurrentIngredients() {
        return Collections.unmodifiableList(currentIngredients);
    }

    public int getCount() {
        return currentIngredients.size();
    }

    public boolean isFull() {
        return currentIngredients.size() >= MAX_INGREDIENTS;
    }

    public boolean isEmpty() {
        return currentIngredients.isEmpty();
    }
}
