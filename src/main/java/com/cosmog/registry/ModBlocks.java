package com.cosmog.registry;

import com.cosmog.CosmogMod;
import com.cosmog.block.CosmicGatewayBlock;
import com.cosmog.block.StarbloomBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CosmogMod.MODID);

    public static final RegistryObject<Block> STARDUST_ORE = BLOCKS.register("stardust_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.END_STONE)
                    .strength(3.5F, 9.0F).requiresCorrectToolForDrops().lightLevel(s -> 5),
                    UniformInt.of(2, 5)));

    public static final RegistryObject<Block> STARBLOOM = BLOCKS.register("starbloom",
            () -> new StarbloomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE).noCollission().instabreak()
                    .sound(SoundType.GRASS).lightLevel(s -> 10).pushReaction(PushReaction.DESTROY)));

    /** Portal fisico: clic derecho para ir a The Cosmog (y volver). */
    public static final RegistryObject<Block> COSMIC_GATEWAY = BLOCKS.register("cosmic_gateway",
            () -> new CosmicGatewayBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)
                    .strength(8.0F, 1200.0F).lightLevel(s -> 12)));
}
