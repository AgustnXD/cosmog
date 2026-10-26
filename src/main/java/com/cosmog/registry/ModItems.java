package com.cosmog.registry;

import com.cosmog.CosmogMod;
import com.cosmog.item.AmuletItem;
import com.cosmog.item.TrueAdeSoulItem;
import com.cosmog.item.AstralArmorMaterial;
import com.cosmog.item.EclipseArmorMaterial;
import com.cosmog.item.EclipseTier;
import com.cosmog.item.RiftCompassItem;
import com.cosmog.item.UmbraKeyItem;
import com.cosmog.item.VoidArmorMaterial;
import com.cosmog.item.VoidTier;
import com.cosmog.item.AstralKeyItem;
import com.cosmog.item.AstralTier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CosmogMod.MODID);

    // ---- materiales
    public static final RegistryObject<Item> STARDUST_SHARD = ITEMS.register("stardust_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASTRAL_INGOT = ITEMS.register("astral_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    /** Lo suelta el Regulador de Cosmos; se usa para craftear la Espada Astral. */
    public static final RegistryObject<Item> COSMIC_CORE = ITEMS.register("cosmic_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // ---- llave de la dimension
    public static final RegistryObject<Item> ASTRAL_KEY = ITEMS.register("astral_key",
            () -> new AstralKeyItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    // ---- amuletos Curios (slot "charm")
    /** Nivel 1: Regeneracion I + Vision nocturna */
    public static final RegistryObject<Item> STAR_HEART = ITEMS.register("star_heart",
            () -> new AmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
                    .buff(() -> MobEffects.REGENERATION, 0)
                    .buff(() -> MobEffects.NIGHT_VISION, 0));

    /** Nivel 2: lo anterior + Velocidad + Resistencia al fuego */
    public static final RegistryObject<Item> NOVA_HEART = ITEMS.register("nova_heart",
            () -> new AmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE))
                    .buff(() -> MobEffects.REGENERATION, 0)
                    .buff(() -> MobEffects.NIGHT_VISION, 0)
                    .buff(() -> MobEffects.MOVEMENT_SPEED, 0)
                    .buff(() -> MobEffects.FIRE_RESISTANCE, 0));

    /** Nivel 3: Absorcion III, Velocidad III, Regeneracion V, Resistencia II (+ vision nocturna y resist. al fuego) */
    public static final RegistryObject<Item> COSMIC_HEART = ITEMS.register("cosmic_heart",
            () -> new AmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
                    .buff(() -> MobEffects.ABSORPTION, 2)
                    .buff(() -> MobEffects.MOVEMENT_SPEED, 2)
                    .buff(() -> MobEffects.REGENERATION, 4)
                    .buff(() -> MobEffects.DAMAGE_RESISTANCE, 1)
                    .buff(() -> MobEffects.NIGHT_VISION, 0)
                    .buff(() -> MobEffects.FIRE_RESISTANCE, 0));

    /** Nivel 4 (extra, propuesto): Cosmic Heart + Fuerza II + Prisa II */
    public static final RegistryObject<Item> ASTRAL_HEART = ITEMS.register("astral_heart",
            () -> new AmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
                    .buff(() -> MobEffects.ABSORPTION, 2)
                    .buff(() -> MobEffects.MOVEMENT_SPEED, 2)
                    .buff(() -> MobEffects.REGENERATION, 4)
                    .buff(() -> MobEffects.DAMAGE_RESISTANCE, 1)
                    .buff(() -> MobEffects.NIGHT_VISION, 0)
                    .buff(() -> MobEffects.FIRE_RESISTANCE, 0)
                    .buff(() -> MobEffects.DAMAGE_BOOST, 1)
                    .buff(() -> MobEffects.DIG_SPEED, 1));

    // ---- armadura astral
    public static final RegistryObject<Item> ASTRAL_HELMET = ITEMS.register("astral_helmet",
            () -> new ArmorItem(AstralArmorMaterial.INSTANCE, ArmorItem.Type.HELMET,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ASTRAL_CHESTPLATE = ITEMS.register("astral_chestplate",
            () -> new ArmorItem(AstralArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ASTRAL_LEGGINGS = ITEMS.register("astral_leggings",
            () -> new ArmorItem(AstralArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ASTRAL_BOOTS = ITEMS.register("astral_boots",
            () -> new ArmorItem(AstralArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // ---- espada astral: 40 de dano (1 + 3 + 36)
    public static final RegistryObject<Item> ASTRAL_SWORD = ITEMS.register("astral_sword",
            () -> new SwordItem(AstralTier.INSTANCE, 3, -2.4F,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // ---- items de bloques
    public static final RegistryObject<Item> STARDUST_ORE = ITEMS.register("stardust_ore",
            () -> new BlockItem(ModBlocks.STARDUST_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> COSMIC_GATEWAY = ITEMS.register("cosmic_gateway",
            () -> new BlockItem(ModBlocks.COSMIC_GATEWAY.get(), new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> STARBLOOM = ITEMS.register("starbloom",
            () -> new BlockItem(ModBlocks.STARBLOOM.get(), new Item.Properties()));

    // ---- huevo (solo para pruebas/creativo)
    public static final RegistryObject<Item> COSMOS_REGULATOR_SPAWN_EGG = ITEMS.register("cosmos_regulator_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.COSMOS_REGULATOR, 0x07070C, 0xFF8A2A, new Item.Properties()));

    // ---- discos de musica (los suelta el Regulador de Cosmos, uno al azar)
    public static final RegistryObject<Item> MUSIC_DISC_COSMOG = ITEMS.register("music_disc_cosmog",
            () -> new RecordItem(12, ModSounds.DISC_COSMOG, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3360));
    public static final RegistryObject<Item> MUSIC_DISC_STARFALL = ITEMS.register("music_disc_starfall",
            () -> new RecordItem(11, ModSounds.DISC_STARFALL, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3360));
    public static final RegistryObject<Item> MUSIC_DISC_NEBULA = ITEMS.register("music_disc_nebula",
            () -> new RecordItem(10, ModSounds.DISC_NEBULA, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3360));
    public static final RegistryObject<Item> MUSIC_DISC_EVENT_HORIZON = ITEMS.register("music_disc_event_horizon",
            () -> new RecordItem(9, ModSounds.DISC_EVENT_HORIZON, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), 3040));

    // ================= THE UMBRA =================
    public static final RegistryObject<Item> VOID_FRAGMENT = ITEMS.register("void_fragment",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DARK_INGOT = ITEMS.register("dark_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> CORRUPT_CORE = ITEMS.register("corrupt_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> DARK_ORB = ITEMS.register("dark_orb",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ECLIPSE_TEMPLATE = ITEMS.register("eclipse_template",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ADE_SOUL = ITEMS.register("ade_soul",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TRUE_ADE_SOUL = ITEMS.register("true_ade_soul",
            () -> new TrueAdeSoulItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> UMBRA_KEY = ITEMS.register("umbra_key",
            () -> new UmbraKeyItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> RIFT_COMPASS = ITEMS.register("rift_compass",
            () -> new RiftCompassItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> VOID_HELMET = ITEMS.register("void_helmet",
            () -> new ArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> VOID_CHESTPLATE = ITEMS.register("void_chestplate",
            () -> new ArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> VOID_LEGGINGS = ITEMS.register("void_leggings",
            () -> new ArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> VOID_BOOTS = ITEMS.register("void_boots",
            () -> new ArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> VOID_SWORD = ITEMS.register("void_sword",
            () -> new SwordItem(VoidTier.INSTANCE, 3, -2.4F, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // ---- fusion ECLIPSE (Astral + Vacio): el doble de dano / proteccion
    public static final RegistryObject<Item> ECLIPSE_BLADE = ITEMS.register("eclipse_blade",
            () -> new SwordItem(EclipseTier.INSTANCE, 3, -2.2F, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ECLIPSE_HELMET = ITEMS.register("eclipse_helmet",
            () -> new ArmorItem(EclipseArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ECLIPSE_CHESTPLATE = ITEMS.register("eclipse_chestplate",
            () -> new ArmorItem(EclipseArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ECLIPSE_LEGGINGS = ITEMS.register("eclipse_leggings",
            () -> new ArmorItem(EclipseArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ECLIPSE_BOOTS = ITEMS.register("eclipse_boots",
            () -> new ArmorItem(EclipseArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    /** Regeneracion X, Velocidad X, Resistencia III, Absorcion V (+ vision nocturna y resistencia al fuego) */
    public static final RegistryObject<Item> ECLIPSE_HEART = ITEMS.register("eclipse_heart",
            () -> new AmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
                    .buff(() -> MobEffects.REGENERATION, 9)
                    .buff(() -> MobEffects.MOVEMENT_SPEED, 9)
                    .buff(() -> MobEffects.DAMAGE_RESISTANCE, 2)
                    .buff(() -> MobEffects.ABSORPTION, 4)
                    .buff(() -> MobEffects.NIGHT_VISION, 0)
                    .buff(() -> MobEffects.FIRE_RESISTANCE, 0));

    // ---- bloques de The Umbra
    public static final RegistryObject<Item> DARK_TILES = ITEMS.register("dark_tiles",
            () -> new BlockItem(ModBlocks.DARK_TILES.get(), new Item.Properties()));
    public static final RegistryObject<Item> DARK_FLOOR_TILES = ITEMS.register("dark_floor_tiles",
            () -> new BlockItem(ModBlocks.DARK_FLOOR_TILES.get(), new Item.Properties()));
    public static final RegistryObject<Item> DIM_LAMP = ITEMS.register("dim_lamp",
            () -> new BlockItem(ModBlocks.DIM_LAMP.get(), new Item.Properties()));
    public static final RegistryObject<Item> DIM_LAMP_OFF = ITEMS.register("dim_lamp_off",
            () -> new BlockItem(ModBlocks.DIM_LAMP_OFF.get(), new Item.Properties()));
    public static final RegistryObject<Item> HIDDEN_CACHE = ITEMS.register("hidden_cache",
            () -> new BlockItem(ModBlocks.HIDDEN_CACHE.get(), new Item.Properties()));
    public static final RegistryObject<Item> UMBRA_GATEWAY = ITEMS.register("umbra_gateway",
            () -> new BlockItem(ModBlocks.UMBRA_GATEWAY.get(), new Item.Properties().rarity(Rarity.RARE)));

    // ---- huevos de prueba
    public static final RegistryObject<Item> ANTI_REGULATOR_SPAWN_EGG = ITEMS.register("anti_regulator_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ANTI_REGULATOR, 0xF2F8FF, 0x46E1E6, new Item.Properties()));
    public static final RegistryObject<Item> ADE_SPAWN_EGG = ITEMS.register("ade_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ADE, 0xE8EEF8, 0x6ED7F0, new Item.Properties()));
    public static final RegistryObject<Item> STALKER_SPAWN_EGG = ITEMS.register("stalker_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.STALKER, 0x050505, 0xE8E8F0, new Item.Properties()));
    public static final RegistryObject<Item> SCREAMER_SPAWN_EGG = ITEMS.register("screamer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SCREAMER, 0xBEBEB6, 0x000000, new Item.Properties()));

    public static final RegistryObject<Item> TRUE_ADE_SPAWN_EGG = ITEMS.register("true_ade_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TRUE_ADE, 0xF4AAC6, 0xA0D0EE, new Item.Properties()));
}
