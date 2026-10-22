package com.cosmog.event;

import com.cosmog.CosmogMod;
import com.cosmog.registry.ModDimensions;
import com.cosmog.registry.ModEntities;
import com.cosmog.util.CosmogTeleporter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Eventos del bus de Forge. */
@Mod.EventBusSubscriber(modid = CosmogMod.MODID)
public class ModEvents {

    /**
     * En The Cosmog no puede entrar NINGUN mob hostil (salvo el Regulador de Cosmos)
     * ni mobs que vengan de otros mods (pasivos vanilla si estan permitidos).
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getLevel().dimension() != ModDimensions.COSMOG) {
            return;
        }
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }
        if (mob.getType() == ModEntities.COSMOS_REGULATOR.get()) {
            return;
        }
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        boolean fromOtherMod = id != null && !id.getNamespace().equals("minecraft");
        if (mob instanceof Enemy || fromOtherMod) {
            event.setCanceled(true);
        }
    }

    /** Dimension tranquila: si caes al vacio, vuelves a la isla del jefe en lugar de morir. */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) {
            return;
        }
        if (event.player instanceof ServerPlayer sp
                && sp.level().dimension() == ModDimensions.COSMOG
                && sp.tickCount % 10 == 0
                && sp.getY() < 8.0D) {
            CosmogTeleporter.rescue(sp);
        }
    }
}
