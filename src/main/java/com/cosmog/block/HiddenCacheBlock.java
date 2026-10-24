package com.cosmog.block;

import com.cosmog.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Parece un azulejo normal; con clic derecho se "abre" y suelta un poco de comida. Al romperlo tambien (loot table). */
public class HiddenCacheBlock extends Block {
    private static final ItemLike[] FOOD = {Items.BREAD, Items.BAKED_POTATO, Items.APPLE, Items.CARROT, Items.COOKED_CHICKEN, Items.PUMPKIN_PIE};

    public HiddenCacheBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        RandomSource random = level.random;
        int stacks = 1 + random.nextInt(2);
        for (int i = 0; i < stacks; i++) {
            popResource(level, pos.relative(hit.getDirection()), new ItemStack(FOOD[random.nextInt(FOOD.length)], 1 + random.nextInt(2)));
        }
        level.setBlockAndUpdate(pos, ModBlocks.DARK_TILES.get().defaultBlockState());
        level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.7F, 0.8F);
        return InteractionResult.CONSUME;
    }
}
