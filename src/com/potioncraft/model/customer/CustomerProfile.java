package com.potioncraft.model.customer;

/** Canonical thirteen-customer roster; appearance and personality share one identity. */
public enum CustomerProfile {
    ELDER_HERBALIST("คุณยายแม่มดสมุนไพร", "Elder Herbalist", CustomerRarity.COMMON, "ใจเย็น: ความอดทนลดช้าลง 20%", .80, 0, 1, 148, 24),
    APPRENTICE_MAGE("จอมเวทฝึกหัด", "Apprentice Mage", CustomerRarity.COMMON, "ขี้กังวล: รีบร้อนขึ้นเมื่อเวลาเหลือครึ่งเดียว", .95, 1, 1, 162, 27),
    MUSHROOM_GATHERER("สาวน้อยเก็บเห็ด", "Mushroom Gatherer", CustomerRarity.COMMON, "อดทน: ไม่ตกใจเมื่อปรุงพลาด", .90, 0, 0, 140, 23),
    LOCAL_FARMER("ชาวนาท้องถิ่น", "Local Farmer", CustomerRarity.COMMON, "ตรงเวลา: รอช้าลง แต่เสียเวลาเพิ่มเมื่อยาพลาด", .85, 0, 3, 174, 27),
    MERCENARY_KNIGHT("อัศวินรับจ้าง", "Mercenary Knight", CustomerRarity.RARE, "มีวินัย: ความอดทนคงที่ ทิปเพิ่ม 10%", 1, 2, 1, 184, 28),
    ROGUE_SHADOW("โจรเงาพเนจร", "Rogue Shadow", CustomerRarity.RARE, "ไม่ชอบรอ: ความอดทนลดเร็วขึ้น 15%", 1.15, 0, 1, 170, 26),
    ELVEN_RANGER("นักพิทักษ์ป่าเอลฟ์", "Elven Ranger", CustomerRarity.RARE, "รีบกลับป่า: ทิปเพิ่ม 25% ถ้าเสิร์ฟก่อนครึ่งเวลา", 1, 3, 1, 182, 28),
    TRAVELING_MERCHANT("นักการค้าแดนไกล", "Traveling Merchant", CustomerRarity.RARE, "พิถีพิถัน: ยาพลาดเสียความอดทนเพิ่ม 4 วินาที", .90, 0, 4, 166, 26),
    ORC_BERSERKER("นักรบเบอร์เซิร์กเกอร์", "Orc Berserker", CustomerRarity.EPIC, "เลือดร้อน: รีบขึ้นเมื่อเวลาเหลือครึ่งเดียว", 1, 1, 3, 208, 31),
    HIGH_PRIESTESS("นักบวชศักดิ์สิทธิ์", "High Priestess", CustomerRarity.EPIC, "เมตตา: ไม่เสียความอดทนเพิ่มเมื่อปรุงพลาด", .90, 0, 0, 178, 27),
    DARK_PALADIN("อัศวินเกราะดำ", "Paladin of Darkness", CustomerRarity.EPIC, "นิ่งสงบ: ทนรอนานกว่าแขก Epic คนอื่น", .80, 0, 1, 198, 30),
    ARCHMAGE_ALBUS("อาจารย์ใหญ่โรงเรียนเวทมนตร์", "Archmage Albus", CustomerRarity.LEGENDARY, "สุขุม: ความอดทนลดช้าลง 10%", .90, 0, 1, 188, 29),
    FAE_QUEEN("ราชินีภูติแห่งป่าลึก", "Fae Queen", CustomerRarity.LEGENDARY, "ใจกว้าง: ทิปเพิ่ม 25% ถ้าเสิร์ฟก่อนครึ่งเวลา", .90, 3, 2, 176, 28);

    private final String name, title, trait;
    private final CustomerRarity rarity;
    private final double decay;
    private final int behavior, mistakePenalty, heightCm, spriteHeight;
    CustomerProfile(String name,String title,CustomerRarity rarity,String trait,double decay,int behavior,int penalty,int heightCm,int spriteHeight){
        this.name=name;this.title=title;this.rarity=rarity;this.trait=trait;
        this.decay=decay;this.behavior=behavior;this.mistakePenalty=penalty;
        this.heightCm=heightCm;this.spriteHeight=spriteHeight;
    }
    public String getName(){return name;}
    public String getTitle(){return title;}
    public String getTrait(){return trait;}
    public CustomerRarity getRarity(){return rarity;}
    /** Approximate design height, separate from hats, crowns, weapons and wings. */
    public int getHeightCm(){return heightCm;}
    public int getSpriteHeight(){return spriteHeight;}
    public String getAssetKey(){return name().toLowerCase(java.util.Locale.ROOT);}
    public double decay(double remaining){return decay*(behavior==1 && remaining<.5?1.4:1);}
    public double reward(double remaining){return behavior==2?1.1:behavior==3 && remaining>=.5?1.25:1;}
    public int getMistakePenalty(){return mistakePenalty;}
}