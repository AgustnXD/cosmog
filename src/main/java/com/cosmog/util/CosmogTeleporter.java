package com.cosmog.util;

import com.cosmog.entity.AdeEntity;
import com.cosmog.registry.ModBlocks;
import com.cosmog.registry.ModDimensions;
import com.cosmog.registry.ModItems;
import com.cosmog.world.PoolroomsGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

/** Viaje a The Cosmog (isla del jefe) y a The Umbra (sala de llegada). */
public class CosmogTeleporter {
    /** Posibles centros de la isla del jefe (la estructura se genera rotada al azar alrededor del chunk 0,0). */
    private static final int[][] CANDIDATE_CENTERS = {{30, 30}, {-30, 30}, {-30, -30}, {30, -30}};
    private static final int ARRIVAL_OFFSET = 20;
    private static final int FALLBACK_Y = 130;

    /** Si ya estas en la dimension 'target' te devuelve a tu cama/spawn; si no, te lleva a ella. */
    public static boolean travel(ServerPlayer sp, ResourceKey<Level> target) {
        MinecraftServer server = sp.getServer();
        if (server == null || sp.getCooldowns().isOnCooldown(ModItems.ASTRAL_KEY.get())) {
            return false;
        }
        ServerLevel from = sp.serverLevel();
        List<AdeEntity> pets = from.getEntitiesOfClass(AdeEntity.class, sp.getBoundingBox().inflate(24.0D),
                ade -> ade.isOwnedBy(sp) && !ade.isOrderedToSit());

        if (from.dimension() == target) {
            ServerLevel dest = server.getLevel(sp.getRespawnDimension());
            BlockPos pos = sp.getRespawnPosition();
            if (dest == null || dest.dimension() == ModDimensions.COSMOG || dest.dimension() == ModDimensions.UMBRA) {
                dest = server.overworld();
                pos = null;
            }
            if (pos == null) {
                pos = dest.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, dest.getSharedSpawnPos());
            }
            sp.teleportTo(dest, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, sp.getYRot(), sp.getXRot());
        } else {
            ServerLevel dest = server.getLevel(target);
            if (dest == null) {
                sp.displayClientMessage(Component.translatable("message.cosmog.no_dimension"), true);
                return false;
            }
            if (target == ModDimensions.UMBRA) {
                arriveUmbra(sp, dest);
            } else {
                arriveCosmog(sp, dest);
            }
        }

        ServerLevel now = sp.serverLevel();
        if (now != from) {   // Ade viaja contigo
            for (AdeEntity pet : pets) {
                Entity moved = pet.changeDimension(now);
                if (moved != null) {
                    moved.moveTo(sp.getX() + 1.0D, sp.getY(), sp.getZ() + 1.0D);
                }
            }
        }
        now.playSound(null, sp.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F);
        sp.getCooldowns().addCooldown(ModItems.ASTRAL_KEY.get(), 100);
        return true;
    }

    /** Si caes al vacio de The Cosmog, el cosmos te devuelve a la isla del jefe. */
    public static void rescue(ServerPlayer sp) {
        arriveCosmog(sp, sp.serverLevel());
        sp.fallDistance = 0.0F;
        sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
        sp.displayClientMessage(Component.translatable("message.cosmog.rescued"), true);
    }

    // ------------------------------------------------------------------ The Umbra
    private static void arriveUmbra(ServerPlayer sp, ServerLevel level) {
        level.getChunk(0, 0);   // genera la sala de llegada
        BlockPos gate = new BlockPos(18, PoolroomsGenerator.FY + 1, 16);
        level.setBlockAndUpdate(gate, ModBlocks.UMBRA_GATEWAY.get().defaultBlockState());
        sp.teleportTo(level, 16.5D, PoolroomsGenerator.FY + 1.0D, 16.5D, 90.0F, 0.0F);
    }

    // ------------------------------------------------------------------ The Cosmog
    private static void arriveCosmog(ServerPlayer sp, ServerLevel level) {
        BlockPos spot = findCosmogArrival(level);
        BlockPos gate = spot.east(2);
        if (level.getBlockState(gate.below()).isFaceSturdy(level, gate.below(), Direction.UP)
                && level.getBlockState(gate).canBeReplaced()) {
            level.setBlockAndUpdate(gate, ModBlocks.COSMIC_GATEWAY.get().defaultBlockState());
        }
        sp.teleportTo(level, spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, 90.0F, 0.0F);
    }

    private static BlockPos findShrineCenter(ServerLevel level) {
        for (int[] c : CANDIDATE_CENTERS) {
            level.getChunk(c[0] >> 4, c[1] >> 4);   // genera el chunk (y la estructura)
            for (int y = 118; y <= 142; y++) {
                BlockPos p = new BlockPos(c[0], y, c[1]);
                if (level.getBlockState(p).is(Blocks.CRYING_OBSIDIAN)) {
                    return p;
                }
            }
        }
        return null;
    }

    private static BlockPos findCosmogArrival(ServerLevel level) {
        BlockPos center = findShrineCenter(level);
        int x = center != null ? center.getX() + ARRIVAL_OFFSET : 0;
        int z = center != null ? center.getZ() : 0;
        level.getChunk(x >> 4, z >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (y <= level.getMinBuildHeight() + 2) {   // vacio / estructura no encontrada: plataforma de emergencia
            y = center != null ? center.getY() + 1 : FALLBACK_Y;
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    level.setBlockAndUpdate(new BlockPos(x + dx, y - 1, z + dz), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
                    level.setBlockAndUpdate(new BlockPos(x + dx, y, z + dz), Blocks.AIR.defaultBlockState());
                    level.setBlockAndUpdate(new BlockPos(x + dx, y + 1, z + dz), Blocks.AIR.defaultBlockState());
                }
            }
        }
        return new BlockPos(x, y, z);
    }
}
