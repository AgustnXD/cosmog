package com.cosmog.registry;

import com.cosmog.CosmogMod;
import com.cosmog.entity.AdeEntity;
import com.cosmog.entity.AntiRegulatorEntity;
import com.cosmog.entity.CosmosRegulatorEntity;
import com.cosmog.entity.ScreamerEntity;
import com.cosmog.entity.StalkerEntity;
import com.cosmog.entity.TrueAdeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CosmogMod.MODID);

    public static final RegistryObject<EntityType<CosmosRegulatorEntity>> COSMOS_REGULATOR = ENTITIES.register(
            "cosmos_regulator",
            () -> EntityType.Builder.of(CosmosRegulatorEntity::new, MobCategory.MONSTER)
                    .sized(1.5F, 4.5F)
                    .fireImmune()
                    .clientTrackingRange(12)
                    .build(CosmogMod.MODID + ":cosmos_regulator"));

    public static final RegistryObject<EntityType<AntiRegulatorEntity>> ANTI_REGULATOR = ENTITIES.register(
            "anti_regulator",
            () -> EntityType.Builder.of(AntiRegulatorEntity::new, MobCategory.MONSTER)
                    .sized(1.5F, 4.5F).fireImmune().clientTrackingRange(12)
                    .build(CosmogMod.MODID + ":anti_regulator"));

    public static final RegistryObject<EntityType<AdeEntity>> ADE = ENTITIES.register(
            "ade",
            () -> EntityType.Builder.of(AdeEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F).fireImmune().clientTrackingRange(10)
                    .build(CosmogMod.MODID + ":ade"));

    public static final RegistryObject<EntityType<StalkerEntity>> STALKER = ENTITIES.register(
            "stalker",
            () -> EntityType.Builder.of(StalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 3.6F).clientTrackingRange(12)
                    .build(CosmogMod.MODID + ":stalker"));

    public static final RegistryObject<EntityType<ScreamerEntity>> SCREAMER = ENTITIES.register(
            "screamer",
            () -> EntityType.Builder.of(ScreamerEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10)
                    .build(CosmogMod.MODID + ":screamer"));

    public static final RegistryObject<EntityType<TrueAdeEntity>> TRUE_ADE = ENTITIES.register(
            "true_ade",
            () -> EntityType.Builder.of(TrueAdeEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F).fireImmune().clientTrackingRange(10)
                    .build(CosmogMod.MODID + ":true_ade"));
}
