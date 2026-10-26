package com.cosmog.util;

import com.cosmog.entity.AntiRegulatorEntity;
import com.cosmog.entity.ScreamerEntity;
import com.cosmog.entity.StalkerEntity;
import com.cosmog.registry.ModEntities;
import com.cosmog.world.PoolroomsGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Los mobs de The Umbra no aparecen "solos": un director los invoca muy de vez en cuando
 * (Acechador unico cada 7-15 min, Griton unico cada 4-11 min) y NUNCA durante el combate contra el jefe.
 */
public class UmbraHaunting {
    private static final Map<UUID, int[]> TIMERS = new HashMap<>();

    public static void tick(ServerPlayer sp) {
        if (sp.tickCount % 20 != 0 || sp.isSpectator()) {
            return;
        }
        ServerLevel level = sp.serverLevel();
        int[] t = TIMERS.computeIfAbsent(sp.getUUID(),
                id -> new int[]{4800 + sp.getRandom().nextInt(4800), 3000 + sp.getRandom().nextInt(6000)});

        if (inBossZone(level, sp)) {   // sin sustos durante el jefe
            purge(level, sp);
            return;
        }
        t[0] -= 20;
        t[1] -= 20;
        if (t[0] <= 0) {
            if (level.getEntitiesOfClass(StalkerEntity.class, new AABB(sp.blockPosition()).inflate(300.0D)).isEmpty()) {
                BlockPos spot = findSpot(level, sp, 22.0D, 34.0D, false, 4);
                if (spot != null) {
                    StalkerEntity e = ModEntities.STALKER.get().create(level);
                    if (e != null) {
                        e.moveTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, sp.getRandom().nextFloat() * 360.0F, 0.0F);
                        e.setTarget(sp);
                        level.addFreshEntity(e);
                    }
                }
            }
            t[0] = 9000 + sp.getRandom().nextInt(9000);
        }
        if (t[1] <= 0) {
            if (level.getEntitiesOfClass(ScreamerEntity.class, new AABB(sp.blockPosition()).inflate(300.0D)).isEmpty()) {
                BlockPos spot = findSpot(level, sp, 7.0D, 11.0D, true, 3);
                if (spot != null) {
                    ScreamerEntity e = ModEntities.SCREAMER.get().create(level);
                    if (e != null) {
                        e.moveTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, 0.0F, 0.0F);
                        e.setTarget(sp);
                        level.addFreshEntity(e);
                    }
                }
            }
            t[1] = 5000 + sp.getRandom().nextInt(8000);
        }
    }

    private static boolean inBossZone(ServerLevel level, ServerPlayer sp) {
        double dx = sp.getX() - PoolroomsGenerator.BOSS_X;
        double dz = sp.getZ() - PoolroomsGenerator.BOSS_Z;
        if (dx * dx + dz * dz < 55.0D * 55.0D) {
            return true;
        }
        return !level.getEntitiesOfClass(AntiRegulatorEntity.class, sp.getBoundingBox().inflate(80.0D), Entity::isAlive).isEmpty();
    }

    private static void purge(ServerLevel level, ServerPlayer sp) {
        AABB box = sp.getBoundingBox().inflate(90.0D);
        List<StalkerEntity> stalkers = level.getEntitiesOfClass(StalkerEntity.class, box);
        for (StalkerEntity e : stalkers) {
            e.discard();
        }
        List<ScreamerEntity> screamers = level.getEntitiesOfClass(ScreamerEntity.class, box);
        for (ScreamerEntity e : screamers) {
            e.discard();
        }
    }

    private static BlockPos findSpot(ServerLevel level, ServerPlayer sp, double minD, double maxD, boolean behind, int headroom) {
        double yaw = Math.toRadians(sp.getYRot());
        double baseAngle = Math.atan2(-Math.cos(yaw), Math.sin(yaw));   // direccion "detras" del jugador
        for (int attempt = 0; attempt < 30; attempt++) {
            double angle = behind ? baseAngle + (sp.getRandom().nextDouble() - 0.5D) * 1.6D : sp.getRandom().nextDouble() * Math.PI * 2.0D;
            double dist = minD + sp.getRandom().nextDouble() * (maxD - minD);
            int x = (int) Math.floor(sp.getX() + Math.cos(angle) * dist);
            int z = (int) Math.floor(sp.getZ() + Math.sin(angle) * dist);
            if (!PoolroomsGenerator.isInsideBuilding(x, z)) {
                continue;
            }
            double bx = x - PoolroomsGenerator.BOSS_X;
            double bz = z - PoolroomsGenerator.BOSS_Z;
            if (bx * bx + bz * bz < 60.0D * 60.0D) {
                continue;
            }
            BlockPos pos = new BlockPos(x, PoolroomsGenerator.FY + 1, z);
            if (!level.hasChunkAt(pos) || !level.getBlockState(pos.below()).isSolid()) {
                continue;
            }
            boolean free = true;
            for (int i = 0; i < headroom; i++) {
                if (!level.getBlockState(pos.above(i)).isAir()) {
                    free = false;
                    break;
                }
            }
            if (free) {
                return pos;
            }
        }
        return null;
    }
}
