package com.potioncraft.test;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.game.GameEngine;
import com.potioncraft.model.potion.Ingredient;
import java.nio.file.*;
import java.util.Random;

/** Supply consumption, rejected actions, manual refill and simulation-time boundaries. */
public final class IngredientStockTest {
    private static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    public static void main(String[] args)throws Exception{
        SoundManager.setSoundEnabled(false);
        GameEngine game=new GameEngine(Files.createTempDirectory(Paths.get("build"),"stock-test-").resolve("save"),new Random(4));
        game.loadLevel(0);game.startManual();
        Ingredient h=game.getLevel().getHerbs().get(0),other=game.getLevel().getHerbs().get(1);
        check(game.getIngredientStock(h)==5,"Starts with five");
        check(!game.refillIngredient(h),"Cannot refill nonempty bin");
        for(int i=0;i<3;i++)check(game.addIngredient(h),"Consumes one per accepted pick");
        check(game.getIngredientStock(h)==2,"Three picks leave two");
        check(!game.addIngredient(h) && game.getIngredientStock(h)==2,"Full cauldron does not consume stock");
        game.clearCauldron();check(game.getIngredientStock(h)==2,"Discard does not restore stock");
        for(int i=0;i<2;i++)check(game.addIngredient(h),"Consume remaining two");
        game.clearCauldron();check(!game.addIngredient(h),"Empty bin cannot provide ingredients");
        game.advance(.5);check(game.getIngredientStock(h)==0,"Empty bin never auto-refills");
        check(game.refillIngredient(h),"Click starts refill");
        check(!game.refillIngredient(h),"Repeated click cannot restart refill");
        check(game.addIngredient(other),"Other bins work during refill");
        game.advance(2.99);check(game.getIngredientStock(h)==0 && !game.addIngredient(h),"Cannot pick before three seconds");
        game.pause();double remaining=game.getRefillRemaining(h);game.advance(10);
        check(game.getRefillRemaining(h)==remaining,"Pause freezes refill");game.pause();
        game.advance(.01);check(game.getIngredientStock(h)==5 && !game.isRefilling(h),"Full stock at exactly three seconds");
        check(game.addIngredient(h) && game.getIngredientStock(h)==4,"Picking resumes after refill");
        game.reset();check(game.getIngredientStock(h)==5 && !game.isRefilling(h),"Reset restores supplies");
        game.stop();check(!game.refillIngredient(h),"Cannot refill while stopped");
        System.out.println("PASS: Supply consumption, five-piece capacity, manual three-second refill, pause and reset.");
    }
}
