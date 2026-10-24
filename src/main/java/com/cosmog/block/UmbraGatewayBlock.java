package com.cosmog.block;

import com.cosmog.registry.ModDimensions;
import com.cosmog.util.CosmogTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Portal umbral: clic derecho para viajar a The Umbra (y volver). */
public class UmbraGatewayBlock extends Block {
    public UmbraGatewayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer sp) {
            CosmogTeleporter.travel(sp, ModDimensions.UMBRA);
        }
        return InteractionResult.CONSUME;
    }
}
