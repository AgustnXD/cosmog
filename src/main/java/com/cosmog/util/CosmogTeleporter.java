package com.cosmog.util;

import com.cosmog.registry.ModBlocks;
import com.cosmog.registry.ModDimensions;
import com.cosmog.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

/** Viaje a The Cosmog: siempre llegas a la isla del jefe (a 20 bloques del santuario). */
public class CosmogTeleporter {
    /** Posibles centros de la isla del jefe (la estructura se genera rotada al azar alrededor del chunk 0,0). */
    private static final int[][] CANDIDATE_CENTERS = {{30, 30}, {-30, 30}, {-30, -30}, {30, -30}};
    private static final int ARRIVAL_OFFSET = 20;
    private static final int FALLBACK_Y = 130;

    /** Si estas en The Cosmog te devuelve a tu cama/spawn; si no, te lleva a The Cosmog. */
    public static boolean travel(ServerPlayer sp) {
        MinecraftServer server = sp.getServer();
        if (server == null || sp.getCooldowns().isOnCooldown(ModItems.ASTRAL_KEY.get())) {
            return false;
        }

        if (sp.level().dimension() == ModDimensions.COSMOG) {
            ServerLevel dest = server.getLevel(sp.getRespawnDimension());
            BlockPos pos = sp.getRespawnPosition();
            if (dest == null || dest.dimension() == ModDimensions.COSMOG) {
                dest = server.overworld();
                pos = null;
            }
            if (pos == null) {
                pos = dest.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, dest.getSharedSpawnPos());
            }
            sp.teleportTo(dest, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, sp.getYRot(), sp.getXRot());
        } else {
            ServerLevel dest = server.getLevel(ModDimensions.COSMOG);
            if (dest == null) {
                sp.displayClientMessage(Component.translatable("message.cosmog.no_dimension"), true);
                return false;
            }
            arrive(sp, dest);
        }
        sp.level().playSound(null, sp.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F);
        sp.getCooldowns().addCooldown(ModItems.ASTRAL_KEY.get(), 100);
        return true;
    }

    /** Si caes al vacio, el cosmos te devuelve a la isla del jefe. */
    public static void rescue(ServerPlayer sp) {
        ServerLevel level = sp.serverLevel();
        arrive(sp, level);
        sp.fallDistance = 0.0F;
        sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
        sp.displayClientMessage(Component.translatable("message.cosmog.rescued"), true);
    }

    private static void arrive(ServerPlayer sp, ServerLevel level) {
        BlockPos spot = findArrival(level);
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

    private static BlockPos findArrival(ServerLevel level) {
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
