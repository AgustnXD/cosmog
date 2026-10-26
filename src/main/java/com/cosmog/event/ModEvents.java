package com.cosmog.event;

import com.cosmog.CosmogMod;
import com.cosmog.registry.ModDimensions;
import com.cosmog.registry.ModEntities;
import com.cosmog.registry.ModItems;
import com.cosmog.util.CosmogTeleporter;
import com.cosmog.util.UmbraHaunting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Eventos del bus de Forge. */
@Mod.EventBusSubscriber(modid = CosmogMod.MODID)
public class ModEvents {

    /**
     * - The Cosmog: ningun mob hostil (salvo el Regulador) ni mobs de otros mods.
     * - The Umbra: SOLO sus propios mobs (Acechador, Gritón, Anti-Reguladora).
     * - Ade (la companera) puede estar en cualquier dimension.
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)) {
            return;
        }
        EntityType<?> type = mob.getType();
        if (type == ModEntities.ADE.get() || type == ModEntities.TRUE_ADE.get()) {
            return;
        }
        var dimension = event.getLevel().dimension();
        if (dimension == ModDimensions.UMBRA) {
            boolean own = type == ModEntities.STALKER.get() || type == ModEntities.SCREAMER.get()
                    || type == ModEntities.ANTI_REGULATOR.get();
            if (!own) {
                event.setCanceled(true);
            }
        } else if (dimension == ModDimensions.COSMOG) {
            if (type == ModEntities.COSMOS_REGULATOR.get()) {
                return;
            }
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
            boolean fromOtherMod = id != null && !id.getNamespace().equals("minecraft");
            if (mob instanceof Enemy || fromOtherMod) {
                event.setCanceled(true);
            }
        }
    }

    /** Dimension tranquila: si caes al vacio de The Cosmog, vuelves a la isla del jefe en lugar de morir. */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) {
            return;
        }
        if (!(event.player instanceof ServerPlayer sp)) {
            return;
        }
        if (sp.level().dimension() == ModDimensions.COSMOG && sp.tickCount % 10 == 0 && sp.getY() < 8.0D) {
            CosmogTeleporter.rescue(sp);
        } else if (sp.level().dimension() == ModDimensions.UMBRA) {
            UmbraHaunting.tick(sp);
        }
    }

    /** Set completo de armadura Eclipse: la mitad de dano recibido (compensa el limite de 30 puntos de armadura). */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.ECLIPSE_HELMET.get())
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.ECLIPSE_CHESTPLATE.get())
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.ECLIPSE_LEGGINGS.get())
                && entity.getItemBySlot(EquipmentSlot.FEET).is(ModItems.ECLIPSE_BOOTS.get())) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }
}
