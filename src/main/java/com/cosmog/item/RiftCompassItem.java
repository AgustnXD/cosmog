package com.cosmog.item;

import com.cosmog.registry.ModDimensions;
import com.cosmog.world.PoolroomsGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Pista aproximada (direccion y distancia redondeada) hacia la sala del Anti-Regulador. */
public class RiftCompassItem extends Item {
    private static final String[] DIRS = {"n", "ne", "e", "se", "s", "sw", "w", "nw"};

    public RiftCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (level.dimension() != ModDimensions.UMBRA) {
                player.displayClientMessage(Component.translatable("message.cosmog.compass_wrong_dim"), true);
            } else {
                double dx = PoolroomsGenerator.BOSS_X - player.getX();
                double dz = PoolroomsGenerator.BOSS_Z - player.getZ();
                long dist = Math.round(Math.sqrt(dx * dx + dz * dz) / 25.0D) * 25L;
                double angle = Math.toDegrees(Math.atan2(dx, -dz));   // 0 = norte (-Z), 90 = este (+X)
                int idx = (int) Math.round(((angle + 360.0D) % 360.0D) / 45.0D) % 8;
                player.displayClientMessage(Component.translatable("message.cosmog.rift_compass", dist,
                        Component.translatable("direction.cosmog." + DIRS[idx])), true);
            }
            player.getCooldowns().addCooldown(this, 40);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
