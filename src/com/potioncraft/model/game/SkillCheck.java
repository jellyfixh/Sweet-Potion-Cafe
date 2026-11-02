package com.potioncraft.model.game;
import java.util.Random;
import com.potioncraft.model.customer.CustomerRarity;

/** Timing rules stay outside the renderer. */
public final class SkillCheck {
    private final int requiredHits;
    private final double width,speed;
    private final Random random;
    private double elapsed,target;
    private int hits;
    private boolean failed;
    public SkillCheck(CustomerRarity rarity,Random random){
        int tier=rarity.ordinal();
        requiredHits=1+tier+random.nextInt(2);width=.24-tier*.045;
        speed=.74;this.random=random;nextTarget();
    }
    private void nextTarget(){elapsed=0;target=.3+random.nextDouble()*.4;}
    public void update(double seconds){
        if(isDone() || !Double.isFinite(seconds) || seconds<0)return;
        elapsed+=seconds;if(elapsed>=5)failed=true;
    }
    public boolean hit(){
        if(isDone())return false;
        if(Math.abs(getPosition()-target)>width/2){failed=true;return false;}
        hits++;if(hits<requiredHits)nextTarget();return true;
    }
    public double getPosition(){double p=(elapsed*speed)%2;return p<=1?p:2-p;}
    public double getTarget(){return target;}
    public double getWidth(){return width;}
    public double getTimeLeft(){return Math.max(0,5-elapsed);}
    public int getHits(){return hits;}
    public int getRequiredHits(){return requiredHits;}
    public boolean isFailed(){return failed;}
    public boolean isDone(){return failed || hits>=requiredHits;}
}
