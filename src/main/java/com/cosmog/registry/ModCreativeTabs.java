package com.cosmog.registry;

import com.cosmog.CosmogMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CosmogMod.MODID);

    public static final RegistryObject<CreativeModeTab> COSMOG_TAB = TABS.register("cosmog",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cosmog"))
                    .icon(() -> new ItemStack(ModItems.COSMIC_CORE.get()))
                    .displayItems((params, output) -> ModItems.ITEMS.getEntries().forEach(i -> output.accept(i.get())))
                    .build());
}
