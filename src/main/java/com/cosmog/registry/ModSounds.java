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

    public static final RegistryObject<SoundEvent> MUSIC_UMBRA = SOUNDS.register("music.umbra",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CosmogMod.MODID, "music.umbra")));

    public static final RegistryObject<SoundEvent> REGULATOR_AMBIENT = sfx("regulator_ambient");
    public static final RegistryObject<SoundEvent> REGULATOR_HURT = sfx("regulator_hurt");
    public static final RegistryObject<SoundEvent> REGULATOR_DEATH = sfx("regulator_death");
    public static final RegistryObject<SoundEvent> REGULATOR_PULSE = sfx("regulator_pulse");
    public static final RegistryObject<SoundEvent> ANTI_AMBIENT = sfx("anti_ambient");
    public static final RegistryObject<SoundEvent> ANTI_HURT = sfx("anti_hurt");
    public static final RegistryObject<SoundEvent> ANTI_DEATH = sfx("anti_death");
    public static final RegistryObject<SoundEvent> ANTI_PULSE = sfx("anti_pulse");
    public static final RegistryObject<SoundEvent> STALKER_AMBIENT = sfx("stalker_ambient");
    public static final RegistryObject<SoundEvent> SCREAMER_SCREAM = sfx("screamer_scream");
    public static final RegistryObject<SoundEvent> STALKER_STEP = sfx("stalker_step");
    public static final RegistryObject<SoundEvent> STALKER_WHISPER = sfx("stalker_whisper");

    private static RegistryObject<SoundEvent> sfx(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CosmogMod.MODID, name)));
    }
}
