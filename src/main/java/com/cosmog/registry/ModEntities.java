package com.cosmog.registry;

import com.cosmog.CosmogMod;
import com.cosmog.entity.CosmosRegulatorEntity;
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
}
