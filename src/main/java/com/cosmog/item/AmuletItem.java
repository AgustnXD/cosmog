package com.cosmog.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Amuleto Curios (slot "charm") que aplica efectos mientras esta equipado. */
public class AmuletItem extends Item implements ICurioItem {
    public record Buff(Supplier<MobEffect> effect, int amplifier) {}

    private final List<Buff> buffs = new ArrayList<>();

    public AmuletItem(Properties properties) {
        super(properties);
    }

    /** amplifier: 0 = nivel I, 1 = nivel II, 2 = nivel III, 4 = nivel V ... */
    public AmuletItem buff(Supplier<MobEffect> effect, int amplifier) {
        buffs.add(new Buff(effect, amplifier));
        return this;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide() || entity.tickCount % 20 != 0) {
            return;
        }
        for (Buff b : buffs) {
            // duracion > 200 ticks para que la vision nocturna no parpadee; sin particulas
            entity.addEffect(new MobEffectInstance(b.effect().get(), 260, b.amplifier(), true, false, true));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.AQUA));
    }
}
