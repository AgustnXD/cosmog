package com.cosmog.item;

import com.cosmog.entity.TrueAdeEntity;
import com.cosmog.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Clic derecho en el suelo: llama a True Ade (conserva afecto, vida, fuerza y recuerdos si ya existian). */
public class TrueAdeSoulItem extends Item {
    public TrueAdeSoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        TrueAdeEntity ade = ModEntities.TRUE_ADE.get().create(level);
        if (ade == null) {
            return InteractionResult.FAIL;
        }
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ade.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot() + 180.0F, 0.0F);
        ade.tame(player);
        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AdeData")) {
            ade.loadStats(tag.getCompound("AdeData"));
        }
        level.addFreshEntity(ade);
        stack.shrink(1);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
