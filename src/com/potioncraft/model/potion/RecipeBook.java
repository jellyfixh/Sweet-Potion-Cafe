package com.potioncraft.model.potion;

import java.awt.Color;
import java.util.*;

/** Registry and validator for the shop's 24 named potion recipes. */
public class RecipeBook {
    private static final List<Potion> ALL_RECIPES = new ArrayList<>();

    // Keep the original public fields for game systems that refer to a featured recipe.
    public static final Potion HEALTH_POTION = potion("ELIXIR_OF_THE_DEEP", "Elixir of the Deep", "โพชั่นห้วงลึกมหาสมุทร",
        "น้ำยาสีฟ้าเขียวเรืองแสงจากหยดน้ำตานางเงือกและเห็ดเรืองแสง", 0x16b9b4, 0x60fff0,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.LUMINOUS_SHROOM);
    public static final Potion MANA_POTION = potion("TIDAL_VITALITY", "Tidal Vitality", "โพชั่นวารีบริสุทธิ์",
        "สายน้ำฟื้นพลังที่พันรอบด้วยรากไม้พันปี", 0x1768bd, 0x65acff,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.MILLENNIAL_ROOT);
    public static final Potion FIRE_RESIST_POTION = potion("PRISM_PROTECTION", "Prism Protection", "โล่หนามมังกร",
        "เกราะสีรุ้งจากผลึกเวทและเกล็ดมังกร", 0xd3369d, 0xff72d6,
        Ingredient.RAINBOW_CRYSTAL, Ingredient.DRAGON_SCALE);
    public static final Potion INVISIBILITY_POTION = potion("LUNAR_AURORA", "Lunar Aurora", "โพชั่นผลึกจันทรา",
        "หมอกแสงจันทร์สีพาสเทลในขวดผลึกรูปดาว", 0xb995df, 0xf0c8ff,
        Ingredient.RAINBOW_CRYSTAL, Ingredient.MOONLIGHT_DUST);
    public static final Potion BERSERK_POTION = potion("STARLIGHT_SLUMBER", "Starlight Slumber", "โพชั่นห้วงนิทรา",
        "โอสถสีเงินเย็นเยียบประดับแสงดาว", 0xb9d6e8, 0xd4f3ff,
        Ingredient.MOONLIGHT_DUST, Ingredient.FROST_PETAL);
    public static final Potion FROST_POTION = potion("PERMAFROST_TONIC", "Permafrost Tonic", "โพชั่นระเบิดน้ำแข็ง",
        "น้ำยาเยือกแข็งในขวดเหลี่ยมเกาะผลึกน้ำแข็ง", 0x54b7e8, 0xa0eaff,
        Ingredient.FROST_PETAL, Ingredient.LUMINOUS_SHROOM);
    public static final Potion STAMINA_POTION = potion("SOLAR_RENEWAL", "Solar Renewal", "โพชั่นเพลิงสุริยัน",
        "พลังสุริยันสีทองในขวดสมุนไพรเก่า", 0xe8a931, 0xffd858,
        Ingredient.MILLENNIAL_ROOT, Ingredient.SUNFLOWER_POLLEN);
    public static final Potion PHOENIX_ELIXIR = potion("FLAME_OF_DAWN", "Flame of Dawn", "โพชั่นแห่งรุ่งอรุณ",
        "เปลวอรุณสีส้มในขวดมีหนามเกล็ดมังกร", 0xe95d25, 0xff963f,
        Ingredient.SUNFLOWER_POLLEN, Ingredient.DRAGON_SCALE);
    public static final Potion ANCIENT_BLAZE = potion("ANCIENT_BLAZE", "Ancient Blaze", "โพชั่นรากเพลิงดึกดำบรรพ์",
        "แก่นรากไม้สีทับทิมแตกร้าวเป็นแสงเพลิง", 0x8f2637, 0xff5940,
        Ingredient.MILLENNIAL_ROOT, Ingredient.DRAGON_SCALE);
    public static final Potion MOONLIGHT_SPORE = potion("MOONLIGHT_SPORE", "Moonlight Spore", "โพชั่นสปอร์เรืองแสง",
        "สปอร์เรืองแสงสีม่วงในโหลกลม", 0x8245c5, 0x80dfff,
        Ingredient.MOONLIGHT_DUST, Ingredient.LUMINOUS_SHROOM);
    public static final Potion SIRENS_FROZEN_HEART = potion("SIRENS_FROZEN_HEART", "Siren's Frozen Heart", "โพชั่นหัวใจนางเงือกเยือกแข็ง",
        "หัวใจผลึกน้ำแข็งกลั่นจากน้ำตานางเงือกและกลีบเหมันต์", 0x69c9f0, 0x9beeff,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.FROST_PETAL, Ingredient.LUMINOUS_SHROOM);
    public static final Potion CELESTIAL_OCEAN_BREEZE = potion("CELESTIAL_OCEAN_BREEZE", "Celestial Ocean Breeze", "โพชั่นสายลมดารา",
        "สายลมวารีสีอะความารีนประดับประกายดาวสีรุ้ง", 0x29c8bd, 0x7cfff0,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.RAINBOW_CRYSTAL, Ingredient.MOONLIGHT_DUST);
    public static final Potion LEVIATHANS_BLESSING = potion("LEVIATHANS_BLESSING", "Leviathan's Blessing", "โพชั่นพรแห่งเลเวียธาน",
        "พรแห่งสมุทรในขวดรากไม้และเกล็ดมังกร", 0x31c1d7, 0x82f7ff,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.MILLENNIAL_ROOT, Ingredient.DRAGON_SCALE);
    public static final Potion WORLD_TREE_SAP = potion("WORLD_TREE_SAP", "World Tree Sap", "โพชั่นยางไม้พฤกษาโลก",
        "ยางไม้สีมรกตที่เก็บประกายผลึกสีรุ้ง", 0x3eae67, 0x87ed9c,
        Ingredient.MILLENNIAL_ROOT, Ingredient.RAINBOW_CRYSTAL, Ingredient.LUMINOUS_SHROOM);
    public static final Potion WINTER_DRAGON_BREATH = potion("WINTER_DRAGON_BREATH", "Winter Dragon Breath", "โพชั่นลมหายใจมังกรเหมันต์",
        "ลมหายใจมังกรสองสี ทั้งน้ำแข็งและเปลวสุริยัน", 0x4686ca, 0xff9a64,
        Ingredient.FROST_PETAL, Ingredient.SUNFLOWER_POLLEN, Ingredient.DRAGON_SCALE);
    public static final Potion SOLAR_PHOENIX_ELIXIR = potion("SOLAR_PHOENIX_ELIXIR", "Solar Phoenix Elixir", "โพชั่นจุติฟีนิกซ์สุริยัน",
        "โอสถฟีนิกซ์ทองแดงแดงที่ส่องสว่างดุจดวงตะวัน", 0xd48b23, 0xffdd62,
        Ingredient.RAINBOW_CRYSTAL, Ingredient.SUNFLOWER_POLLEN, Ingredient.DRAGON_SCALE);
    public static final Potion ASTRAL_DREAMSCAPE = potion("ASTRAL_DREAMSCAPE", "Astral Dreamscape", "โพชั่นมิติแห่งความฝัน",
        "ความฝันสีม่วงจักรวาลที่ลอยอยู่ในขวดแก้วกลม", 0x674cae, 0xc894ff,
        Ingredient.RAINBOW_CRYSTAL, Ingredient.MOONLIGHT_DUST, Ingredient.LUMINOUS_SHROOM);
    public static final Potion FROSTFIRE_AEGIS = potion("FROSTFIRE_AEGIS", "Frostfire Aegis", "โพชั่นเกราะเพลิงเยือกแข็ง",
        "เกราะผลึกที่หลอมไฟและน้ำแข็งเข้าด้วยกัน", 0x89dce4, 0xb6f5fa,
        Ingredient.RAINBOW_CRYSTAL, Ingredient.FROST_PETAL, Ingredient.DRAGON_SCALE);
    public static final Potion MOONLIT_TIDE_TONIC = potion("MOONLIT_TIDE_TONIC", "Moonlit Tide Tonic", "โพชั่นกระแสวารีจันทร์กระจ่าง",
        "กระแสน้ำสีน้ำเงินแซฟไฟร์ในหมอกเงินจันทร์", 0x226da9, 0x94cbff,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.MOONLIGHT_DUST, Ingredient.LUMINOUS_SHROOM);
    public static final Potion ANCIENT_SUN_REMEDY = potion("ANCIENT_SUN_REMEDY", "Ancient Sun Remedy", "โพชั่นโอสถสุริยะพันปี",
        "โอสถอำพันห่อด้วยรากไม้และผงทองสุริยัน", 0xc98028, 0xffc45f,
        Ingredient.MILLENNIAL_ROOT, Ingredient.MOONLIGHT_DUST, Ingredient.SUNFLOWER_POLLEN);
    public static final Potion DRAGON_ROOTS_DRAUGHT = potion("DRAGON_ROOTS_DRAUGHT", "Dragon Roots Draught", "โพชั่นแก่นรากมังกร",
        "น้ำยาสีเลือดหมูในไหหินพันตะไคร่และตรามังกร", 0x70283b, 0xff7767,
        Ingredient.MILLENNIAL_ROOT, Ingredient.LUMINOUS_SHROOM, Ingredient.DRAGON_SCALE);
    public static final Potion GLACIAL_STARLIGHT_BREW = potion("GLACIAL_STARLIGHT_BREW", "Glacial Starlight Brew", "โพชั่นเหมันต์หยุดเวลา",
        "โอสถสีฟ้าซีดที่มีแกนทองส่องแสงเหมือนดาว", 0x9dcfe7, 0xffefae,
        Ingredient.MOONLIGHT_DUST, Ingredient.FROST_PETAL, Ingredient.SUNFLOWER_POLLEN);
    public static final Potion RAINBOW_MIRACLE_ELIXIR = potion("RAINBOW_MIRACLE_ELIXIR", "Rainbow Miracle Elixir", "โพชั่นปาฏิหาริย์แห่งรุ้ง",
        "ผลึกหลายสีพร่างพรายในขวดคริสตัล", 0x92c9cf, 0xfff08a,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.RAINBOW_CRYSTAL, Ingredient.SUNFLOWER_POLLEN);
    public static final Potion ABYSSAL_DRAGON_TEARS = potion("ABYSSAL_DRAGON_TEARS", "Abyssal Dragon Tears", "โพชั่นน้ำตามังกรห้วงลึก",
        "น้ำตาสีครามเข้มของมังกรห้วงลึกในขวดราชัน", 0x292d72, 0x9188ed,
        Ingredient.MERMAIDS_TEARDROP, Ingredient.FROST_PETAL, Ingredient.DRAGON_SCALE);

    static {
        ALL_RECIPES.addAll(Arrays.asList(HEALTH_POTION, MANA_POTION, FIRE_RESIST_POTION, INVISIBILITY_POTION,
            BERSERK_POTION, FROST_POTION, STAMINA_POTION, PHOENIX_ELIXIR, ANCIENT_BLAZE, MOONLIGHT_SPORE,
            SIRENS_FROZEN_HEART, CELESTIAL_OCEAN_BREEZE, LEVIATHANS_BLESSING, WORLD_TREE_SAP,
            WINTER_DRAGON_BREATH, SOLAR_PHOENIX_ELIXIR, ASTRAL_DREAMSCAPE, FROSTFIRE_AEGIS,
            MOONLIT_TIDE_TONIC, ANCIENT_SUN_REMEDY, DRAGON_ROOTS_DRAUGHT, GLACIAL_STARLIGHT_BREW,
            RAINBOW_MIRACLE_ELIXIR, ABYSSAL_DRAGON_TEARS));
    }

    private static Potion potion(String id,String en,String th,String desc,int liquid,int glow,Ingredient... ingredients) {
        Color c=new Color(liquid),g=new Color(glow);
        return new Potion(id,en,th,desc,"✦",c,g,Arrays.asList(ingredients));
    }
    public static List<Potion> getAllRecipes() { return Collections.unmodifiableList(ALL_RECIPES); }
    public static Potion brew(List<Ingredient> ingredients) {
        if (ingredients==null || ingredients.isEmpty()) return Potion.createFailedSludge(Collections.emptyList());
        List<Ingredient> input=new ArrayList<>(ingredients); input.sort(Comparator.comparing(Ingredient::name));
        for(Potion recipe:ALL_RECIPES){
            List<Ingredient> expected=new ArrayList<>(recipe.getRecipe()); expected.sort(Comparator.comparing(Ingredient::name));
            if(input.equals(expected)) return recipe;
        }
        return Potion.createFailedSludge(ingredients);
    }
}
