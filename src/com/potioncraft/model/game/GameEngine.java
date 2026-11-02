package com.potioncraft.model.game;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.customer.*;
import com.potioncraft.model.potion.*;
import javax.swing.Timer;
import java.nio.file.*;
import java.util.*;

/** Owns campaign simulation; Swing and Java2D are views, not a game engine dependency. */
public class GameEngine {
    public static final int MAX_CUSTOMER_SLOTS=4, INITIAL_REPUTATION=5, TICK_INTERVAL_MS=20;
    public static final int INGREDIENT_CAPACITY=5;
    public static final double REFILL_SECONDS=3;
    private final int[] stock=new int[Ingredient.values().length];
    private final double[] refillRemaining=new double[Ingredient.values().length];
    private final List<Customer> customers=new ArrayList<>();
    private final List<GameEventListener> listeners=new ArrayList<>();
    private final Cauldron cauldron=new Cauldron();
    private final CampaignProgress progress;
    private final Random random;
    private final Timer timer;
    private Level level=new Level(0);
    private Potion held;
    private SkillCheck skill;
    private int gold,reputation=5,score,combo,served,lost,spawned,clean,mistakes;
    private boolean running,paused,finished;
    private double elapsed,spawnCountdown;
    private long lastTick;
    private String message="เลือกด่านเพื่อเปิดร้าน";

    public GameEngine(){this(Paths.get("save","progress.properties"),new Random());}
    public GameEngine(Path savePath,Random random){
        this.progress=new CampaignProgress(savePath);this.random=random;
        resetStock();
        timer=new Timer(TICK_INTERVAL_MS,e->{
            long now=System.nanoTime();double dt=(now-lastTick)/1_000_000_000.0;lastTick=now;advance(dt);
        });
    }
    public void addListener(GameEventListener listener){listeners.add(listener);}
    public void removeListener(GameEventListener listener){listeners.remove(listener);}
    public boolean loadLevel(int index){
        if(!progress.isUnlocked(index))return false;
        stop();level=new Level(index);customers.clear();cauldron.clear();held=null;skill=null;
        gold=score=combo=served=lost=spawned=clean=mistakes=0;reputation=5;
        elapsed=0;spawnCountdown=.7;paused=finished=false;
        resetStock();
        message="เปิดร้านแล้ว! เลือกลูกค้า ดูสูตร แล้วใส่สมุนไพร";notifyAllViews();return true;
    }
    public void start(){
        if(finished)return;
        running=true;paused=false;lastTick=System.nanoTime();timer.start();notifyAllViews();
    }
    /** Start without a wall-clock timer for deterministic simulations and offscreen QA. */
    public void startManual(){if(!finished){running=true;paused=false;timer.stop();}}
    public void pause(){
        if(!running || finished)return;paused=!paused;lastTick=System.nanoTime();
    }
    public void stop(){running=false;timer.stop();}
    public void reset(){loadLevel(level.getIndex());}
    private boolean canAct(){return running && !paused && !finished;}

    public void advance(double seconds){
        if(!canAct() || !Double.isFinite(seconds) || seconds<=0)return;
        // Bounded simulation steps keep spawn and expiration ordering stable after UI stalls.
        double remaining=Math.min(seconds,120);
        while(remaining>0 && canAct()){
            double dt=Math.min(.05,remaining);remaining-=dt;step(dt);
        }
        notifyStats();
    }
    private void step(double dt){
        elapsed+=dt;
        for(Ingredient herb:level.getHerbs()){
            int i=herb.ordinal();
            if(refillRemaining[i]>0){
                refillRemaining[i]=Math.max(0,refillRemaining[i]-dt);
                if(refillRemaining[i]<1e-9){refillRemaining[i]=0;stock[i]=INGREDIENT_CAPACITY;notifyCauldron();}
            }
        }
        for(Customer c:new ArrayList<>(customers)){
            c.updatePatience(dt);
            if(c.isAngry()){
                customers.remove(c);lost++;reputation--;combo=0;
                message=c.getName()+" รอไม่ไหวและออกจากร้าน";
                for(GameEventListener l:listeners)l.onCustomerAngryLeft(c,reputation);
                SoundManager.playAngrySound();
            }
        }
        if(reputation<=0 || elapsed>=level.getDuration()){finish();return;}
        if(skill!=null){skill.update(dt);if(skill.isFailed())resolveBrew(false);}
        if(spawned<level.getCustomerCount() && customers.size()<MAX_CUSTOMER_SLOTS){
            spawnCountdown-=dt;
            if(spawnCountdown<=0){
                Set<CustomerProfile> present=new HashSet<>();
                for(Customer waiting:customers)present.add(waiting.getProfile());
                Customer c=CustomerFactory.spawn(level.getWorld(),spawned,level.getRecipes(),random,present);
                customers.add(c);spawned++;spawnCountdown=level.getSpawnDelay();
                for(GameEventListener l:listeners)l.onCustomerSpawned(c);
            }
        }
        if(spawned==level.getCustomerCount() && customers.isEmpty())finish();
    }
    private void finish(){
        if(finished)return;
        finished=true;running=false;paused=false;timer.stop();skill=null;
        progress.record(level.getIndex(),getEarnedStars());
        message=getEarnedStars()>0?"ผ่านด่าน! บันทึกดาวแล้ว":"ยังไม่ผ่าน ลองปรุงใหม่อีกครั้ง";
        for(GameEventListener l:listeners)l.onGameOver(score,served,lost);
    }
    public boolean addIngredient(Ingredient ingredient){
        if(!canAct() || skill!=null || held!=null || !level.getHerbs().contains(ingredient))return false;
        if(getIngredientStock(ingredient)==0 || isRefilling(ingredient))return false;
        if(cauldron.addIngredient(ingredient)){stock[ingredient.ordinal()]--;SoundManager.playBubbleSound();notifyCauldron();return true;}
        return false;
    }
    private void resetStock(){Arrays.fill(stock,INGREDIENT_CAPACITY);Arrays.fill(refillRemaining,0);}
    public int getIngredientStock(Ingredient ingredient){return level.getHerbs().contains(ingredient)?stock[ingredient.ordinal()]:0;}
    public double getRefillRemaining(Ingredient ingredient){return refillRemaining[ingredient.ordinal()];}
    public boolean isRefilling(Ingredient ingredient){return getRefillRemaining(ingredient)>0;}
    public boolean refillIngredient(Ingredient ingredient){
        if(!canAct() || !level.getHerbs().contains(ingredient) || getIngredientStock(ingredient)!=0 || isRefilling(ingredient))return false;
        refillRemaining[ingredient.ordinal()]=REFILL_SECONDS;
        message="กำลังเติม "+ingredient.getThaiName()+" · รอ 3 วินาที";notifyCauldron();return true;
    }
    public void clearCauldron(){if(canAct() && skill==null){cauldron.clear();notifyCauldron();}}
    /** Begins the timing challenge. A potion is produced only after the last successful hit. */
    public Potion brew(){beginBrew();return null;}
    public boolean beginBrew(){
        Potion recipe=RecipeBook.brew(cauldron.getCurrentIngredients());
        Customer target=customers.stream().filter(c->c.getDesiredPotion().equals(recipe)).findFirst().orElse(null);
        return beginBrew(target);
    }
    public boolean beginBrew(Customer target){
        if(!canAct() || skill!=null || held!=null)return false;
        if(new HashSet<>(cauldron.getCurrentIngredients()).size()<2){message="ต้องใส่สมุนไพรต่างชนิดอย่างน้อย 2 ชนิด";return false;}
        CustomerRarity rarity=target!=null && customers.contains(target)?target.getRarity():CustomerRarity.COMMON;
        skill=new SkillCheck(rarity,random);message="กดแล้วปล่อย SPACE หรือคลิกหลอด เมื่อขีดอยู่ในช่วงสีเขียว พลาดครั้งเดียวยาเสีย";return true;
    }
    public void skillHit(){
        if(!canAct() || skill==null)return;
        skill.hit();
        if(skill.isDone())resolveBrew(!skill.isFailed());
    }
    private void resolveBrew(boolean success){
        List<Ingredient> ingredients=new ArrayList<>(cauldron.getCurrentIngredients());
        held=success?cauldron.brew():Potion.createFailedSludge(ingredients);
        cauldron.clear();skill=null;
        if(held.isFailed()){
            mistakes++;combo=0;for(Customer c:customers)c.onBrewFailed();
            message="ยาพัง! เททิ้งแล้วปรุงใหม่ ลูกค้ายังรออยู่";SoundManager.playErrorSound();
        }else{clean++;message="ปรุงสำเร็จ: "+held.getThaiName()+" — คลิกลูกค้าเพื่อเสิร์ฟ";SoundManager.playBrewSuccessSound();}
        notifyAllViews();
    }
    public void trashHeldPotion(){
        if(!canAct() || skill!=null)return;
        held=null;cauldron.clear();message="ล้างหม้อแล้ว เลือกส่วนผสมใหม่ได้";notifyAllViews();
    }
    public boolean serveCustomer(Customer customer){
        if(!canAct() || skill!=null || customer==null || customer.isAngry() || !customers.contains(customer) || held==null)return false;
        if(!held.equals(customer.getDesiredPotion())){
            customer.updatePatience(4);combo=0;mistakes++;message="ยาผิดออเดอร์! ลูกค้าเสียความอดทน";SoundManager.playErrorSound();return false;
        }
        int reward=customer.getPersonalityReward();customer.markServed();customers.remove(customer);
        combo++;reward+=Math.min(60,(combo-1)*5);gold+=reward;score+=reward*2;served++;held=null;
        message=customer.getName()+": "+customer.getServedDialogue()+" +"+reward+" G";
        SoundManager.playCoinSound();
        for(GameEventListener l:listeners)l.onCustomerServed(customer,reward,combo);
        notifyAllViews();if(spawned==level.getCustomerCount() && customers.isEmpty())finish();return true;
    }
    private void notifyCauldron(){for(GameEventListener l:listeners)l.onCauldronUpdated();}
    private void notifyStats(){for(GameEventListener l:listeners)l.onStatsUpdated(gold,reputation,score,combo,level.getIndex()+1);}
    private void notifyAllViews(){notifyCauldron();for(GameEventListener l:listeners)l.onPotionBrewed(held);notifyStats();}
    public Level getLevel(){return level;}
    public CampaignProgress getProgress(){return progress;}
    public SkillCheck getSkillCheck(){return skill;}
    public List<Customer> getActiveCustomers(){return Collections.unmodifiableList(customers);}
    public Cauldron getCauldron(){return cauldron;}
    public Potion getCurrentHeldPotion(){return held;}
    public int getGold(){return gold;}
    public int getReputation(){return reputation;}
    public int getScore(){return score;}
    public int getCombo(){return combo;}
    public int getDay(){return level.getIndex()+1;}
    public int getTotalCustomersServed(){return served;}
    public int getTotalCustomersLost(){return lost;}
    public int getSpawnedCount(){return spawned;}
    public int getCleanBrews(){return clean;}
    public int getMistakes(){return mistakes;}
    public boolean[] getMissions(){return level.missions(served,gold,clean,mistakes);}
    public int getEarnedStars(){int n=0;for(boolean m:getMissions())if(m)n++;return n;}
    public double getTimeRemaining(){return Math.max(0,level.getDuration()-elapsed);}
    public String getMessage(){return message;}
    public boolean isRunning(){return running;}
    public boolean isPaused(){return paused;}
    public boolean isGameOver(){return finished;}
}
