package com.cosmog.registry;

import com.cosmog.CosmogMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CosmogMod.MODID);

    /** Musica de fondo del bioma (assets/cosmog/sounds/the_cosmog.ogg, ver sounds.json) */
    public static final RegistryObject<SoundEvent> COSMOG_MUSIC = SOUNDS.register("music.cosmog",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CosmogMod.MODID, "music.cosmog")));

    public static final RegistryObject<SoundEvent> DISC_COSMOG = disc("cosmog");
    public static final RegistryObject<SoundEvent> DISC_STARFALL = disc("starfall");
    public static final RegistryObject<SoundEvent> DISC_NEBULA = disc("nebula");
    public static final RegistryObject<SoundEvent> DISC_EVENT_HORIZON = disc("event_horizon");

    private static RegistryObject<SoundEvent> disc(String name) {
        return SOUNDS.register("music_disc." + name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CosmogMod.MODID, "music_disc." + name)));
    }
}
