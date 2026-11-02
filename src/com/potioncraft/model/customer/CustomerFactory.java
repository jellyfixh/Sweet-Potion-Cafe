package com.potioncraft.model.customer;
import com.potioncraft.model.potion.*;
import java.util.*;

/** Factory separates roster data from polymorphic tier behavior. */
public final class CustomerFactory {
    private CustomerFactory() {}
    public static Customer create(CustomerProfile p,Potion potion) {
        String n=p.getName(),t=p.getTitle(),wait="ขอ"+potion.getThaiName()+"ด้วยนะ";
        Customer c;
        switch(p.getRarity()) {
            case RARE: c=new RareCustomer(n,t,"",potion,wait,"ฝีมือยอดเยี่ยม!","ต้องไปแล้ว!"); break;
            case EPIC: c=new EpicCustomer(n,t,"",potion,wait,"โอสถชั้นเลิศ!","ข้ารอไม่ไหวแล้ว!"); break;
            case LEGENDARY: c=new LegendaryCustomer(n,t,"",potion,wait,"สมกับคำร่ำลือ!","หมดเวลาของเจ้าแล้ว!"); break;
            default: c=new CommonCustomer(n,t,"",potion,wait,"ขอบคุณนะ!","ไว้คราวหน้าละกัน!");
        }
        c.setProfile(p);return c;
    }
    public static Customer spawn(int world,int arrival,List<Potion> available,Random random) {
        return spawn(world,arrival,available,random,Collections.emptySet());
    }
    public static Customer spawn(int world,int arrival,List<Potion> available,Random random,Set<CustomerProfile> present) {
        if(available.isEmpty())throw new IllegalArgumentException("No craftable recipes");
        List<CustomerProfile> pool=new ArrayList<>();
        for(CustomerProfile p:CustomerProfile.values())if(p.getRarity().ordinal()<world && !present.contains(p))pool.add(p);
        if(pool.isEmpty())throw new IllegalArgumentException("All eligible customers are already in the shop");
        if(arrival%4==3){
            List<CustomerProfile> newest=new ArrayList<>();
            for(CustomerProfile p:pool)if(p.getRarity().ordinal()==world-1)newest.add(p);
            if(!newest.isEmpty())pool=newest;
        }
        return create(pool.get(random.nextInt(pool.size())),available.get(random.nextInt(available.size())));
    }
    public static Customer spawnRandomCustomer(int level) {
        return spawn(Math.min(4,Math.max(1,level)),0,RecipeBook.getAllRecipes(),new Random());
    }
}
