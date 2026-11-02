package com.potioncraft.test;

import com.potioncraft.audio.SoundManager;
import com.potioncraft.model.customer.*;
import com.potioncraft.model.game.*;
import com.potioncraft.model.potion.*;
import java.nio.file.*;
import java.util.*;

/** Integration checks exercise player actions and actual campaign progression. */
public final class CampaignTest {
    private static int checks;
    public static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    public static void completeBrew(GameEngine game,Potion potion){
        boolean wait=false;
        for(Ingredient h:potion.getRecipe())if(game.getIngredientStock(h)==0){
            if(!game.isRefilling(h))check(game.refillIngredient(h),"Empty supply starts refill");
            wait=true;
        }
        if(wait)game.advance(GameEngine.REFILL_SECONDS);
        for(Ingredient h:potion.getRecipe())check(game.addIngredient(h),"Ingredient accepted");
        check(game.beginBrew(),"Begin challenge");
        while(game.getSkillCheck()!=null){
            SkillCheck skill=game.getSkillCheck();
            int guard=0;
            while(Math.abs(skill.getPosition()-skill.getTarget())>.012 && !skill.isDone() && guard++<1000)game.advance(.005);
            check(guard<1000,"Timing cursor reaches target");game.skillHit();
        }
        check(potion.equals(game.getCurrentHeldPotion()),"All successful checks produce requested potion");
    }
    private static void checkSkillTiers(){
        double previousWidth=1;
        for(CustomerRarity rarity:CustomerRarity.values()){
            Random random=new Random(732);
            Set<Integer> counts=new HashSet<>();
            double sum=0;
            for(int i=0;i<200;i++){
                SkillCheck skill=new SkillCheck(rarity,random);
                int hits=skill.getRequiredHits();counts.add(hits);sum+=hits;
                check(hits>=1 && hits<=5,"Random checks stay within 1–5");
                check(hits>=1+rarity.ordinal() && hits<=2+rarity.ordinal(),"Check count follows customer rarity");
            }
            check(counts.size()==2,"Each rarity randomizes its check count");
            check(Math.abs(sum/200-(1.5+rarity.ordinal()))<.15,"Higher rarity increases average check count");
            SkillCheck skill=new SkillCheck(rarity,new Random(7));
            check(skill.getWidth()<previousWidth,"Higher rarity narrows the target");previousWidth=skill.getWidth();
            double firstTarget=skill.getTarget();
            skill.update(firstTarget/.74);check(skill.hit(),"First accurate hit succeeds");
            check(!skill.isDone(),"Multi-check brew waits for every hit");
            check(skill.getTarget()!=firstTarget,"Following check randomizes target");
            check(!skill.hit() && skill.isFailed() && skill.isDone(),"One late miss immediately ends the entire brew");
            int successes=skill.getHits();skill.update(.5);skill.hit();
            check(skill.getHits()==successes && skill.isFailed(),"A failed brew cannot recover");
            SkillCheck timeout=new SkillCheck(rarity,new Random(7));timeout.update(5);
            check(timeout.isFailed(),"Unanswered check spoils at the deadline for every rarity");
        }
    }
    public static void main(String[] args)throws Exception{
        SoundManager.setSoundEnabled(false);
        checkSkillTiers();
        check(Level.WORLD_COUNT==4 && Level.STAGES_PER_WORLD==5 && Level.TOTAL_LEVELS==20 && Level.MAX_STARS==60,"Campaign is four chapters of five stages, 60 stars");
        for(int world=0;world<4;world++)for(int stage=0;stage<5;stage++){
            Level l=new Level(world*5+stage);
            check(l.getWorld()==world+1 && l.getStage()==stage+1,"Chapter and stage boundaries");
        }
        boolean invalidLevel=false;
        try{new Level(20);}catch(IllegalArgumentException expected){invalidLevel=true;}
        check(invalidLevel,"There is no twenty-first stage");
        check(Ingredient.values().length==8,"Exactly eight ingredients");
        check(RecipeBook.getAllRecipes().size()==24,"Exactly 24 recipes");
        Set<String> signatures=new HashSet<>(),reachable=new HashSet<>();
        for(Potion p:RecipeBook.getAllRecipes()){
            check(new HashSet<>(p.getRecipe()).size()>=2,"At least two distinct herbs");
            List<Ingredient> reversed=new ArrayList<>(p.getRecipe());Collections.reverse(reversed);
            check(RecipeBook.brew(reversed).equals(p),"Order independent: "+p.getId());
            List<String> names=new ArrayList<>();for(Ingredient h:p.getRecipe())names.add(h.name());Collections.sort(names);
            check(signatures.add(names.toString()),"Unique recipe");
        }
        int[] counts=new int[4];for(CustomerProfile p:CustomerProfile.values())counts[p.getRarity().ordinal()]++;
        check(Arrays.equals(counts,new int[]{4,4,3,2}),"Roster counts 4/4/3/2");
        for(int i=0;i<Level.TOTAL_LEVELS;i++){
            Level l=new Level(i);check(l.getHerbs().size()==6,"Six herbs per stage");
            check(l.getHerbs().equals(new Level(i).getHerbs()),"Reproducible supplies");
            for(Potion p:l.getRecipes()){check(l.getHerbs().containsAll(p.getRecipe()),"Craftable order");reachable.add(p.getId());}
        }
        check(reachable.size()==24,"Every recipe appears in the campaign");
        Customer calm=CustomerFactory.create(CustomerProfile.MUSHROOM_GATHERER,RecipeBook.HEALTH_POTION);
        double before=calm.getCurrentPatienceSeconds();calm.onBrewFailed();
        check(calm.getCurrentPatienceSeconds()==before,"Mushroom gatherer tolerates brewing mistakes");
        Customer picky=CustomerFactory.create(CustomerProfile.TRAVELING_MERCHANT,RecipeBook.HEALTH_POTION);before=picky.getCurrentPatienceSeconds();picky.onBrewFailed();
        check(picky.getCurrentPatienceSeconds()<before,"Merchant reacts to mistakes");
        Path root=Files.createTempDirectory(Paths.get("build"),"campaign-test-");
        Path save=root.resolve("progress.properties");
        Properties oldSave=new Properties();int[] oldStars={3,2,1,3};
        for(int world=0;world<4;world++)for(int stage=0;stage<10;stage++)
            oldSave.setProperty("level."+(world*10+stage),Integer.toString(stage<5?oldStars[world]:0));
        Path legacy=root.resolve("legacy.properties");
        try(java.io.OutputStream out=Files.newOutputStream(legacy)){oldSave.store(out,"Original 40-stage save");}
        CampaignProgress migrated=new CampaignProgress(legacy);
        check(migrated.getTotalStars()==45,"Old save retains first five stages of each chapter");
        for(int world=0;world<4;world++)for(int stage=0;stage<5;stage++)
            check(migrated.getStars(world*5+stage)==oldStars[world],"Old chapter stars map to correct new stage");
        migrated.record(19,3);
        check(new CampaignProgress(legacy).getStars(5)==2,"New save metadata prevents migration twice");
        GameEngine game=new GameEngine(save,new Random(123));
        check(!game.loadLevel(1),"Stage two initially locked");check(game.loadLevel(0),"Stage one open");game.startManual();game.advance(1);
        Customer first=game.getActiveCustomers().get(0);Potion order=first.getDesiredPotion();
        check(!game.addIngredient(Arrays.stream(Ingredient.values()).filter(h->!game.getLevel().getHerbs().contains(h)).findFirst().get()),"Unavailable herb rejected");
        game.pause();double seconds=game.getTimeRemaining(),patience=first.getCurrentPatienceSeconds();
        check(!game.addIngredient(order.getRecipe().get(0)),"No crafting while paused");
        check(!game.beginBrew(),"No challenge while paused");check(!game.serveCustomer(first),"No serving while paused");
        game.advance(10);check(game.getTimeRemaining()==seconds && first.getCurrentPatienceSeconds()==patience,"Pause freezes clocks");game.pause();
        for(Ingredient h:order.getRecipe())game.addIngredient(h);
        check(game.beginBrew(),"Start failed timing example");game.skillHit();
        check(game.getCurrentHeldPotion().isFailed(),"Missed skill check spoils potion");check(game.getMistakes()==1,"Miss counted");
        check(!game.beginBrew(),"Cannot overwrite held potion");game.trashHeldPotion();
        for(Ingredient h:order.getRecipe())game.addIngredient(h);
        game.beginBrew();game.advance(5.1);check(game.getCurrentHeldPotion().isFailed(),"Missed deadline spoils potion");
        game.trashHeldPotion();game.reset();game.startManual();
        // Play every stage through the same public actions used by the UI.
        for(int index=0;index<Level.TOTAL_LEVELS;index++){
            check(game.loadLevel(index),"Stage "+index+" unlocked by previous result");game.startManual();
            int guard=0;
            while(!game.isGameOver() && guard++<10000){
                if(game.getActiveCustomers().isEmpty()){game.advance(.1);continue;}
                Customer c=game.getActiveCustomers().get(0);completeBrew(game,c.getDesiredPotion());
                check(game.serveCustomer(c),"Serve correct order before patience expires");
            }
            check(guard<10000,"Stage terminates");check(game.getEarnedStars()==3,"All three missions achievable at stage "+index);
            check(game.getTotalCustomersLost()==0,"Perfect route has no abandoned orders");
            check(!game.addIngredient(game.getLevel().getHerbs().get(0)),"No actions after result");
        }
        CampaignProgress reload=new CampaignProgress(save);check(reload.getTotalStars()==Level.MAX_STARS,"60 stars survive reload");
        reload.record(0,1);check(new CampaignProgress(save).getStars(0)==3,"Replay cannot lower best score");
        Path oneStarPath=root.resolve("one-star.properties");CampaignProgress oneStar=new CampaignProgress(oneStarPath);
        oneStar.record(0,0);check(!oneStar.isUnlocked(1),"Zero stars do not unlock next");oneStar.record(0,1);
        check(new CampaignProgress(oneStarPath).isUnlocked(1),"One star unlocks next after reload");
        GameEngine single=new GameEngine(root.resolve("actual-one-star.properties"),new Random(123));
        single.loadLevel(0);single.startManual();single.advance(1);
        Customer sc=single.getActiveCustomers().get(0);
        for(Ingredient h:sc.getDesiredPotion().getRecipe())single.addIngredient(h);
        single.beginBrew();single.skillHit();single.trashHeldPotion();
        for(int n=0;n<2;n++){
            while(single.getActiveCustomers().isEmpty())single.advance(.1);
            sc=single.getActiveCustomers().get(0);completeBrew(single,sc.getDesiredPotion());
            check(single.serveCustomer(sc),"One-star route serves two customers");
        }
        single.advance(110);check(single.getEarnedStars()==1,"Actual round passes with exactly one star");
        check(single.getProgress().isUnlocked(1),"Actual one-star round unlocks next stage");single.stop();
        game.loadLevel(0);game.startManual();game.advance(110);
        check(game.isGameOver() && game.getEarnedStars()==0,"Idle shop ends with zero stars");game.stop();
        System.out.println("PASS: "+checks+" checks; all 20 stages completed through brewing and serving; save reload verified.");
    }
}
