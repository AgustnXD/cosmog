package com.cosmog.world;

import com.cosmog.entity.AntiRegulatorEntity;
import com.cosmog.registry.ModBlocks;
import com.cosmog.registry.ModEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * The Umbra: un complejo infinito de salas de piscina (poolrooms) a oscuras.
 * Todo se calcula por formula a partir de (x, y, z): no hay estructuras ni ruido, y es determinista.
 *  - Rejilla de celdas de 32x32. Cada celda es una sala: piscina, sala seca con pilares, sala inundada, tunel o canal.
 *  - Cada pared tiene siempre una puerta en arco, asi que todo el complejo esta conectado.
 *  - La sala del Anti-Regulador (3x3 celdas, cupula) esta lejos del origen: hay que encontrarla.
 */
public class PoolroomsGenerator extends ChunkGenerator {
    public static final Codec<PoolroomsGenerator> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource))
                    .apply(inst, inst.stable(PoolroomsGenerator::new)));

    public static final int FY = 40;            // y del ultimo bloque del suelo
    public static final int S = 32;             // tamano de celda
    public static final int BOSS_CX = 14;       // celda central de la sala del jefe
    public static final int BOSS_CZ = -17;
    public static final int BOSS_X = BOSS_CX * S + 16;   // 464
    public static final int BOSS_Z = BOSS_CZ * S + 16;   // -528

    public PoolroomsGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    // ------------------------------------------------------------------ generacion
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random,
                                                         StructureManager structures, ChunkAccess chunk) {
        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = minX + dx;
                int z = minZ + dz;
                for (int y = 0; y < 128; y++) {
                    BlockState state = blockAt(x, y, z);
                    if (!state.isAir()) {
                        chunk.setBlockState(pos.set(x, y, z), state, false);
                        oceanFloor.update(dx, y, dz, state);
                        worldSurface.update(dx, y, dz, state);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    /** El unico jefe de la dimension aparece al generarse el chunk de su sala. */
    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        ChunkPos cp = region.getCenter();
        if (cp.x == (BOSS_X >> 4) && cp.z == (BOSS_Z >> 4)) {
            AntiRegulatorEntity boss = ModEntities.ANTI_REGULATOR.get().create(region.getLevel());
            if (boss != null) {
                boss.moveTo(BOSS_X + 0.5D, FY + 3.0D, BOSS_Z + 0.5D, 0.0F, 0.0F);
                boss.setPersistenceRequired();
                region.addFreshEntity(boss);
            }
        }
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState random, BiomeManager biomeManager,
                             StructureManager structures, ChunkAccess chunk, GenerationStep.Carving step) {
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState random, ChunkAccess chunk) {
    }

    @Override
    public int getGenDepth() {
        return 128;
    }

    @Override
    public int getSeaLevel() {
        return FY;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return FY + 1;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        BlockState[] column = new BlockState[128];
        for (int y = 0; y < 128; y++) {
            column[y] = blockAt(x, y, z);
        }
        return new NoiseColumn(0, column);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
    }

    // ------------------------------------------------------------------ bloques
    private static BlockState air() { return Blocks.AIR.defaultBlockState(); }
    private static BlockState wall() { return ModBlocks.DARK_TILES.get().defaultBlockState(); }
    private static BlockState floor() { return ModBlocks.DARK_FLOOR_TILES.get().defaultBlockState(); }
    private static BlockState lamp() { return ModBlocks.DIM_LAMP.get().defaultBlockState(); }
    private static BlockState cache() { return ModBlocks.HIDDEN_CACHE.get().defaultBlockState(); }
    private static BlockState water() { return Blocks.WATER.defaultBlockState(); }
    private static BlockState quartz() { return Blocks.SMOOTH_QUARTZ.defaultBlockState(); }

    private static int hash(int a, int b, int c) {
        int h = a * 374761393 + b * 668265263 + c * 1274126177;
        h = (h ^ (h >>> 13)) * 1274126177;
        return h ^ (h >>> 16);
    }

    private static double rand01(int a, int b, int c) {
        return (hash(a, b, c) & 0xFFFFFF) / (double) 0x1000000;
    }

    private static boolean edgeV(int cx, int cz) { return rand01(cx, cz, 10) < 0.62D; }
    private static boolean edgeH(int cx, int cz) { return rand01(cx, cz, 11) < 0.62D; }

    private static boolean inDoor(int c) { return c >= 12 && c <= 19; }

    private static int doorTop(int c) {
        double t = c - 15.5D;
        return FY + 5 + (int) Math.sqrt(Math.max(0.0D, 16.0D - t * t));
    }

    private static boolean inArena(int cx, int cz) {
        return Math.abs(cx - BOSS_CX) <= 1 && Math.abs(cz - BOSS_CZ) <= 1;
    }

    private static int cellType(int cx, int cz) {
        if (cx == 0 && cz == 0) {
            return 3;   // sala de llegada: seca y despejada
        }
        return Math.min(9, (int) (rand01(cx, cz, 2) * 10.0D));
    }

    private static boolean lit(int cx, int cz) {
        return (cx == 0 && cz == 0) || rand01(cx, cz, 3) < 0.40D;
    }

    /** {x0, x1, z0, z1, profundidad} o null */
    private static int[] poolRect(int type) {
        switch (type) {
            case 0: case 1: case 2: return new int[]{9, 22, 9, 22, 4};
            case 5: return new int[]{4, 27, 4, 27, 8};
            case 7: case 8: return new int[]{2, 29, 11, 20, 4};
            default: return null;
        }
    }

    private static boolean hasPillars(int type) {
        return type <= 4 || type == 9;
    }

    private static boolean isPillar(int lx, int lz) {
        return (lx == 6 || lx == 7 || lx == 24 || lx == 25) && (lz == 6 || lz == 7 || lz == 24 || lz == 25);
    }

    public static BlockState blockAt(int x, int y, int z) {
        if (y < 0 || y >= 128) {
            return air();
        }
        int cx = Math.floorDiv(x, S);
        int cz = Math.floorDiv(z, S);
        if (inArena(cx, cz)) {
            return arenaBlock(x, y, z);
        }
        return cellBlock(x, y, z, cx, cz, Math.floorMod(x, S), Math.floorMod(z, S));
    }

    private static BlockState cellBlock(int x, int y, int z, int cx, int cz, int lx, int lz) {
        int type = cellType(cx, cz);
        double u = (lx - 15.5D) / 16.0D;
        int ceil = type == 6 ? FY + 6 : FY + 9 + (int) (5.0D * (1.0D - u * u));

        if (y >= ceil) {
            if (y == ceil && lit(cx, cz)) {
                boolean spot = type == 6 ? (lx == 16 && lz == 16)
                        : ((lx == 8 || lx == 24) && (lz == 8 || lz == 24));
                if (spot) {
                    return lamp();
                }
            }
            return wall();
        }

        boolean corner = lx < 2 && lz < 2;
        boolean wv = lx < 2 && edgeV(cx, cz);
        boolean wh = lz < 2 && edgeH(cx, cz);
        if (corner || wv || wh) {
            if (y <= FY) {
                return floor();
            }
            boolean door = false;
            int top = 0;
            if (!corner) {
                if (wv && inDoor(lz)) {
                    door = true;
                    top = doorTop(lz);
                } else if (wh && inDoor(lx)) {
                    door = true;
                    top = doorTop(lx);
                }
            }
            if (door && y <= top) {
                return air();
            }
            if (!corner && !door && (y == FY + 1 || y == FY + 2) && (hash(x, y, z) & 0x7FF) == 7) {
                return cache();   // cofre camuflado
            }
            return wall();
        }

        if (type == 6) {   // tunel: solo una cruz abierta
            boolean open = Math.abs(lx - 16) <= 2 || Math.abs(lz - 16) <= 2;
            if (!open) {
                return y <= FY ? floor() : wall();
            }
        }

        int[] pr = poolRect(type);
        if (pr != null && lx >= pr[0] && lx <= pr[1] && lz >= pr[2] && lz <= pr[3]) {
            if (y <= FY - pr[4]) {
                return floor();
            }
            if (y <= FY - 1) {
                return water();
            }
            return air();
        }
        if (y <= FY) {
            return floor();
        }
        if (hasPillars(type) && isPillar(lx, lz)) {
            return wall();
        }
        return air();
    }

    private static BlockState arenaBlock(int x, int y, int z) {
        int ax0 = (BOSS_CX - 1) * S;
        int az0 = (BOSS_CZ - 1) * S;
        int ax1 = ax0 + 3 * S;
        int az1 = az0 + 3 * S;
        double dx = x - (BOSS_X + 0.5D);
        double dz = z - (BOSS_Z + 0.5D);
        double r = Math.sqrt(dx * dx + dz * dz);
        int ceil = FY + 16 + (int) (12.0D * Math.max(0.0D, 1.0D - (r * r) / (48.0D * 48.0D)));
        if (y >= ceil) {
            return wall();
        }

        boolean wallX = x < ax0 + 2 || x >= ax1 - 2;
        boolean wallZ = z < az0 + 2 || z >= az1 - 2;
        if (wallX || wallZ) {
            if (y <= FY) {
                return floor();
            }
            if (!(wallX && wallZ)) {
                if (wallX && Math.floorDiv(z, S) == BOSS_CZ && inDoor(Math.floorMod(z, S))
                        && y <= doorTop(Math.floorMod(z, S))) {
                    return air();
                }
                if (wallZ && Math.floorDiv(x, S) == BOSS_CX && inDoor(Math.floorMod(x, S))
                        && y <= doorTop(Math.floorMod(x, S))) {
                    return air();
                }
            }
            return wall();
        }

        boolean bridge = Math.abs(dx) < 4.5D || Math.abs(dz) < 4.5D;
        if (r >= 25.0D && r <= 31.0D && !bridge) {   // foso con 4 puentes
            if (y <= FY - 4) {
                return floor();
            }
            if (y <= FY - 1) {
                return water();
            }
            return air();
        }
        if (r < 6.0D) {   // estrado central
            return y <= FY + 2 ? quartz() : air();
        }
        if (y <= FY) {
            return (r >= 11.0D && r < 12.5D) ? quartz() : floor();
        }
        if (r > 17.0D && r < 23.0D) {   // 8 pilares con luz
            for (int k = 0; k < 8; k++) {
                double a = k * Math.PI / 4.0D;
                if (Math.hypot(dx - 20.0D * Math.cos(a), dz - 20.0D * Math.sin(a)) <= 1.6D) {
                    return y == ceil - 1 ? lamp() : wall();
                }
            }
        }
        return air();
    }
}
