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
 * The Umbra: un complejo LIMINAL y PEQUENO de salas de piscina a oscuras, rodeado por un mar nocturno.
 *  - Edificio de 11x11 celdas de 20x20 bloques (220x220). Fuera: agua negra bajo un cielo estrellado.
 *  - 14 tipos de sala (piscinas redondas, escalonadas, en pendiente, pozo, canales, tuneles, columnas, salas con claraboya...).
 *  - Ventanas de cristal en las paredes exteriores (unas si, otras cerradas) y claraboyas en algunos techos.
 *  - Lamparas: unas encendidas y otras apagadas.
 *  - La sala del Anti-Reguladora (3x3 celdas, cupula) esta en la esquina noreste del edificio.
 * Todo es una formula determinista de (x, y, z).
 */
public class PoolroomsGenerator extends ChunkGenerator {
    public static final Codec<PoolroomsGenerator> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource))
                    .apply(inst, inst.stable(PoolroomsGenerator::new)));

    public static final int FY = 40;                 // y del ultimo bloque del suelo
    public static final int S = 20;                  // tamano de celda
    public static final int BX0 = -100, BZ0 = -100;  // limites del edificio (min inclusivo)
    public static final int BX1 = 122, BZ1 = 122;    // limites del edificio (max exclusivo, incluye muro exterior de 2)
    public static final int BOSS_CX = 4;
    public static final int BOSS_CZ = -4;
    public static final int BOSS_X = BOSS_CX * S + 10;   // 90
    public static final int BOSS_Z = BOSS_CZ * S + 10;   // -70

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

    // ------------------------------------------------------------------ utilidades
    private static BlockState air() { return Blocks.AIR.defaultBlockState(); }
    private static BlockState wall() { return ModBlocks.DARK_TILES.get().defaultBlockState(); }
    private static BlockState floor() { return ModBlocks.DARK_FLOOR_TILES.get().defaultBlockState(); }
    private static BlockState lampOn() { return ModBlocks.DIM_LAMP.get().defaultBlockState(); }
    private static BlockState lampOff() { return ModBlocks.DIM_LAMP_OFF.get().defaultBlockState(); }
    private static BlockState cache() { return ModBlocks.HIDDEN_CACHE.get().defaultBlockState(); }
    private static BlockState water() { return Blocks.WATER.defaultBlockState(); }
    private static BlockState glass() { return Blocks.GLASS.defaultBlockState(); }
    private static BlockState quartz() { return Blocks.SMOOTH_QUARTZ.defaultBlockState(); }

    private static int hash(int a, int b, int c) {
        int h = a * 374761393 + b * 668265263 + c * 1274126177;
        h = (h ^ (h >>> 13)) * 1274126177;
        return h ^ (h >>> 16);
    }

    private static double rand01(int a, int b, int c) {
        return (hash(a, b, c) & 0xFFFFFF) / (double) 0x1000000;
    }

    private static boolean edgeV(int cx, int cz) { return rand01(cx, cz, 10) < 0.72D; }
    private static boolean edgeH(int cx, int cz) { return rand01(cx, cz, 11) < 0.72D; }

    private static boolean inDoor(int c) { return c >= 8 && c <= 11; }

    private static int doorTop(int c) {
        return FY + 5 + ((c == 9 || c == 10) ? 1 : 0);
    }

    private static boolean inArena(int cx, int cz) {
        return Math.abs(cx - BOSS_CX) <= 1 && Math.abs(cz - BOSS_CZ) <= 1;
    }

    /** true si (x,z) esta dentro del edificio (sin contar el muro exterior). */
    public static boolean isInsideBuilding(int x, int z) {
        return x >= BX0 + 2 && x < BX1 - 2 && z >= BZ0 + 2 && z < BZ1 - 2;
    }

    private static int cellType(int cx, int cz) {
        if (cx == 0 && cz == 0) {
            return 3;   // sala de llegada: seca y despejada
        }
        return Math.min(13, (int) (rand01(cx, cz, 2) * 14.0D));
    }

    private static int ceilBonus(int type) {
        switch (type) {
            case 7: return -2;
            case 8: return -1;
            case 10: return 1;
            case 11: return 4;
            case 12: return 2;
            default: return 0;
        }
    }

    private static boolean hasSkylight(int type, int cx, int cz) {
        return type == 11 || (type != 7 && type != 8 && rand01(cx, cz, 6) < 0.08D);
    }

    // ------------------------------------------------------------------ mundo
    public static BlockState blockAt(int x, int y, int z) {
        if (y < 0 || y >= 128) {
            return air();
        }
        if (x < BX0 || x >= BX1 || z < BZ0 || z >= BZ1) {
            return outside(y);
        }
        if (x < BX0 + 2 || x >= BX1 - 2 || z < BZ0 + 2 || z >= BZ1 - 2) {
            return perimeter(x, y, z);
        }
        int cx = Math.floorDiv(x, S);
        int cz = Math.floorDiv(z, S);
        if (inArena(cx, cz)) {
            return arenaBlock(x, y, z);
        }
        return cellBlock(x, y, z, cx, cz, Math.floorMod(x, S), Math.floorMod(z, S));
    }

    /** Mar nocturno alrededor del edificio. */
    private static BlockState outside(int y) {
        if (y <= FY - 8) {
            return floor();
        }
        if (y <= FY - 1) {
            return water();
        }
        return air();
    }

    /** Muro exterior (2 de grosor) con ventanas en algunos tramos. */
    private static BlockState perimeter(int x, int y, int z) {
        int ceil = FY + 9;
        if (y <= FY) {
            return floor();
        }
        if (y >= ceil) {
            return y <= ceil + 2 ? wall() : air();
        }
        boolean westEast = x < BX0 + 2 || x >= BX1 - 2;
        boolean northSouth = z < BZ0 + 2 || z >= BZ1 - 2;
        if (westEast && northSouth) {
            return wall();   // esquina
        }
        int along = westEast ? z : x;
        int side = x < BX0 + 2 ? 0 : (x >= BX1 - 2 ? 1 : (z < BZ0 + 2 ? 2 : 3));
        int seg = Math.floorDiv(along, 10);
        if (rand01(seg, side, 20) < 0.5D) {
            int loc = Math.floorMod(along, 10);
            if (loc >= 2 && loc <= 7 && y >= FY + 2 && y <= FY + 5) {
                return loc == 4 ? wall() : glass();   // un parteluz en el medio
            }
        }
        return wall();
    }

    private static BlockState lampAt(int x, int z, boolean forceOn) {
        return (forceOn || rand01(x, z, 5) < 0.42D) ? lampOn() : lampOff();
    }

    private static BlockState poolColumn(int y, int depth) {
        if (y <= FY - depth) {
            return floor();
        }
        if (y <= FY - 1) {
            return water();
        }
        return air();
    }

    private static BlockState cellBlock(int x, int y, int z, int cx, int cz, int lx, int lz) {
        int type = cellType(cx, cz);
        double u = (lx - 9.5D) / 10.0D;
        int ceil = FY + 8 + ceilBonus(type) + (int) (3.0D * (1.0D - u * u));

        // ---- techo (3 de grosor), claraboyas y lamparas
        if (y >= ceil) {
            if (y > ceil + 2) {
                return air();
            }
            if (hasSkylight(type, cx, cz) && lx >= 7 && lx <= 12 && lz >= 7 && lz <= 12) {
                return y == ceil ? glass() : air();
            }
            if (y == ceil && (lx == 5 || lx == 14) && (lz == 5 || lz == 14)) {
                return lampAt(x, z, cx == 0 && cz == 0);
            }
            return wall();
        }

        // ---- paredes (izquierda y superior de cada celda) con puertas en arco
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
            if (!corner && !door && (y == FY + 1 || y == FY + 2) && (hash(x, y, z) & 0x3FF) == 7) {
                return cache();   // cofre camuflado
            }
            return wall();
        }

        // ---- interior segun el tipo de sala
        double dx = lx - 9.5D;
        double dz = lz - 9.5D;
        double r = Math.sqrt(dx * dx + dz * dz);
        switch (type) {
            case 0: {   // piscina redonda con columna central
                if (r <= 1.7D) {
                    return wall();
                }
                return r <= 6.5D ? poolColumn(y, 3) : (y <= FY ? floor() : air());
            }
            case 1: {   // piscina rectangular con 4 pilares
                if (isPillar2(lx, lz, 3, 16)) {
                    return wall();
                }
                boolean in = lx >= 5 && lx <= 14 && lz >= 5 && lz <= 14;
                return in ? poolColumn(y, 3) : (y <= FY ? floor() : air());
            }
            case 2: {   // piscina escalonada
                boolean in = lx >= 4 && lx <= 15 && lz >= 4 && lz <= 15;
                if (in) {
                    int d = Math.min(Math.min(lx - 4, 15 - lx), Math.min(lz - 4, 15 - lz));
                    return poolColumn(y, 1 + Math.min(3, d));
                }
                return y <= FY ? floor() : air();
            }
            case 3:     // sala seca con pilares
                return isPillar2(lx, lz, 5, 13) ? (y > FY ? wall() : floor()) : (y <= FY ? floor() : air());
            case 4: {   // inundada
                boolean in = lx >= 3 && lx <= 16 && lz >= 3 && lz <= 16;
                return in ? poolColumn(y, 6) : (y <= FY ? floor() : air());
            }
            case 5:     // canal este-oeste
                return (lz >= 7 && lz <= 12 && lx >= 2) ? poolColumn(y, 3) : (y <= FY ? floor() : air());
            case 6:     // canal norte-sur
                return (lx >= 7 && lx <= 12 && lz >= 2) ? poolColumn(y, 3) : (y <= FY ? floor() : air());
            case 7: {   // tunel en cruz (estrecho)
                boolean open = Math.abs(dx) <= 2.0D || Math.abs(dz) <= 2.0D;
                return open ? (y <= FY ? floor() : air()) : (y <= FY ? floor() : wall());
            }
            case 8: {   // cubiculos (cruz ancha)
                boolean open = Math.abs(dx) <= 4.0D || Math.abs(dz) <= 4.0D;
                return open ? (y <= FY ? floor() : air()) : (y <= FY ? floor() : wall());
            }
            case 9: {   // piscina en pendiente
                boolean in = lx >= 4 && lx <= 15 && lz >= 4 && lz <= 15;
                return in ? poolColumn(y, 1 + (lx - 4) / 3) : (y <= FY ? floor() : air());
            }
            case 10: {  // sala de columnas redondas
                if (y > FY) {
                    for (int k = 0; k < 3; k++) {
                        double cxi = 4.5D + 5.0D * k;
                        if (Math.hypot(lx - cxi, lz - 9.5D) <= 1.4D) {
                            return wall();
                        }
                    }
                    return air();
                }
                return floor();
            }
            case 11:    // sala vacia y alta con claraboya
                return y <= FY ? floor() : air();
            case 12: {  // pozo hundido con anillos concentricos
                if (r <= 7.5D) {
                    return poolColumn(y, 1 + Math.min(4, (int) (7.5D - r)));
                }
                return y <= FY ? floor() : air();
            }
            default: {  // 13: doble piscina con banco central
                boolean a = lx >= 3 && lx <= 8 && lz >= 4 && lz <= 15;
                boolean b = lx >= 11 && lx <= 16 && lz >= 4 && lz <= 15;
                if (a || b) {
                    return poolColumn(y, 3);
                }
                if (lx >= 9 && lx <= 10 && lz >= 6 && lz <= 13 && y == FY + 1) {
                    return floor();   // banco
                }
                return y <= FY ? floor() : air();
            }
        }
    }

    private static boolean isPillar2(int lx, int lz, int a, int b) {
        return (lx == a || lx == a + 1 || lx == b || lx == b + 1) && (lz == a || lz == a + 1 || lz == b || lz == b + 1);
    }

    // ------------------------------------------------------------------ sala del jefe
    private static BlockState arenaBlock(int x, int y, int z) {
        int ax0 = (BOSS_CX - 1) * S;
        int az0 = (BOSS_CZ - 1) * S;
        int ax1 = ax0 + 3 * S;
        int az1 = az0 + 3 * S;
        double dx = x - (BOSS_X + 0.5D);
        double dz = z - (BOSS_Z + 0.5D);
        double r = Math.sqrt(dx * dx + dz * dz);
        int ceil = FY + 14 + (int) (7.0D * Math.max(0.0D, 1.0D - (r * r) / (30.0D * 30.0D)));
        if (y >= ceil) {
            return y <= ceil + 2 ? wall() : air();
        }

        boolean wallX = x < ax0 + 2 || x >= ax1 - 2;
        boolean wallZ = z < az0 + 2 || z >= az1 - 2;
        if (wallX || wallZ) {
            if (y <= FY) {
                return floor();
            }
            if (!(wallX && wallZ)) {
                // puertas solo al oeste y al sur (el norte y el este dan al mar)
                if (x < ax0 + 2 && Math.floorDiv(z, S) == BOSS_CZ && inDoor(Math.floorMod(z, S))
                        && y <= doorTop(Math.floorMod(z, S))) {
                    return air();
                }
                if (z >= az1 - 2 && Math.floorDiv(x, S) == BOSS_CX && inDoor(Math.floorMod(x, S))
                        && y <= doorTop(Math.floorMod(x, S))) {
                    return air();
                }
            }
            return wall();
        }

        boolean bridge = Math.abs(dx) < 3.5D || Math.abs(dz) < 3.5D;
        if (r >= 17.0D && r <= 22.0D && !bridge) {   // foso con 4 puentes
            if (y <= FY - 4) {
                return floor();
            }
            if (y <= FY - 1) {
                return water();
            }
            return air();
        }
        if (r < 5.0D) {   // estrado central
            return y <= FY + 2 ? quartz() : air();
        }
        if (y <= FY) {
            return (r >= 8.0D && r < 9.5D) ? quartz() : floor();
        }
        if (r > 11.0D && r < 15.0D) {   // 8 pilares con luz
            for (int k = 0; k < 8; k++) {
                double a = k * Math.PI / 4.0D;
                if (Math.hypot(dx - 13.0D * Math.cos(a), dz - 13.0D * Math.sin(a)) <= 1.5D) {
                    return y == ceil - 1 ? lampOn() : wall();
                }
            }
        }
        return air();
    }
}
