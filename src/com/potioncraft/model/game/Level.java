package com.potioncraft.model.game;
import com.potioncraft.model.potion.*;
import java.util.*;

/** Immutable reproducible stage configuration. */
public final class Level {
    public static final int WORLD_COUNT=4, STAGES_PER_WORLD=5;
    public static final int TOTAL_LEVELS=WORLD_COUNT*STAGES_PER_WORLD, MAX_STARS=TOTAL_LEVELS*3;
    public static final String[] WORLDS = {"ป่ารุ่งอรุณ", "ท่าเรือจันทรา", "ถ้ำผลึก", "พระราชวังมังกร"};
    private final int index;
    private final List<Ingredient> herbs;
    private final List<Potion> recipes;
    public Level(int index) {
        if(index<0 || index>=TOTAL_LEVELS)throw new IllegalArgumentException("Level must be 0.."+(TOTAL_LEVELS-1));
        this.index=index;
        List<Ingredient> stock=new ArrayList<>(Arrays.asList(Ingredient.values()));
        // Retain supplies for the first five stages of each original ten-stage chapter.
        int supplySeedIndex=(getWorld()-1)*10+getStage()-1;
        Collections.shuffle(stock,new Random(7391L+supplySeedIndex*997L));
        herbs=Collections.unmodifiableList(new ArrayList<>(stock.subList(0,6)));
        List<Potion> valid=new ArrayList<>();
        for(Potion p:RecipeBook.getAllRecipes())if(herbs.containsAll(p.getRecipe()))valid.add(p);
        recipes=Collections.unmodifiableList(valid);
        if(recipes.isEmpty())throw new IllegalStateException("Unplayable level");
    }
    public int getIndex(){return index;}
    public int getWorld(){return index/STAGES_PER_WORLD+1;}
    public int getStage(){return index%STAGES_PER_WORLD+1;}
    public String getName(){return WORLDS[getWorld()-1]+"  "+getWorld()+"-"+getStage();}
    public List<Ingredient> getHerbs(){return herbs;}
    public List<Potion> getRecipes(){return recipes;}
    public int getCustomerCount(){return 5+(getStage()-1)/3+getWorld()-1;}
    public int getServeTarget(){return (getCustomerCount()+1)/2;}
    public int getGoldTarget(){return 80+(getWorld()-1)*80+(getStage()-1)*10;}
    public int getCleanTarget(){return Math.min(4,2+getWorld()/2);}
    public double getDuration(){return 75+getWorld()*10+getStage()*2;}
    public double getSpawnDelay(){return Math.max(4.5,9-getWorld()*.6-getStage()*.12);}
    public boolean[] missions(int served,int gold,int clean,int mistakes){
        return new boolean[]{served>=getServeTarget(),served>0 && gold>=getGoldTarget(),
            served>0 && clean>=getCleanTarget() && mistakes==0};
    }
}
