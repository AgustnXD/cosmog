package com.cosmog.entity;

import com.cosmog.registry.ModItems;
import com.cosmog.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * La Anti-Reguladora de Cosmos: contraparte (blanca) del Regulador. Mismas estadisticas.
 * - La barra de vida SOLO aparece para quien le pega.
 * - Pulso del vacio (dano en area, atrae hacia ella y ciega), salto del vacio y fase 2 (<50% vida).
 * - La primera vez que un jugador la derrota recibe el Alma de Ade.
 */
public class AntiRegulatorEntity extends Monster {
    private static final String PERSISTED = "PlayerPersisted";
    private static final String ADE_GIVEN = "cosmog_ade_given";

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.cosmog.anti_regulator"),
            BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10);

    private int pulseCooldown = 100;
    private int stepCooldown = 160;
    private boolean enraged = false;

    public AntiRegulatorEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.bossEvent.setDarkenScreen(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ARMOR, 14.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.tickCount % 20 == 0) {   // la barra se quita a quien se aleja
            for (ServerPlayer p : new ArrayList<>(this.bossEvent.getPlayers())) {
                if (!p.isAlive() || this.distanceToSqr(p) > 4096.0D) {
                    this.bossEvent.removePlayer(p);
                }
            }
        }
        if (!this.enraged && this.getHealth() < this.getMaxHealth() * 0.5F) {
            this.enraged = true;
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
            this.level().playSound(null, this.blockPosition(), ModSounds.ANTI_PULSE.get(), SoundSource.HOSTILE, 3.0F, 0.7F);
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (--this.pulseCooldown <= 0) {
            this.voidPulse();
            this.pulseCooldown = this.enraged ? 70 : 130;
        }
        if (--this.stepCooldown <= 0) {
            if (this.distanceToSqr(target) > 36.0D) {
                this.voidStep(target);
            }
            this.stepCooldown = this.enraged ? 100 : 160;
        }
    }

    /** Implosion: dano magico en area, atrae hacia ella y oscurece la vision. */
    private void voidPulse() {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        float damage = this.enraged ? 16.0F : 10.0F;
        List<Player> players = server.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(12.0D, 4.0D, 12.0D),
                p -> p.isAlive() && !p.isSpectator() && !p.isCreative());
        for (Player p : players) {
            p.hurt(this.damageSources().magic(), damage);
            Vec3 in = this.position().subtract(p.position()).multiply(1.0D, 0.0D, 1.0D);
            if (in.lengthSqr() > 1.0E-4D) {
                in = in.normalize();
            }
            p.push(in.x * 1.1D, 0.3D, in.z * 1.1D);
            p.hurtMarked = true;
            p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false));
        }
        server.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.5D, this.getZ(), 120, 5.0D, 2.0D, 5.0D, 0.15D);
        server.sendParticles(ParticleTypes.GLOW, this.getX(), this.getY() + 2.0D, this.getZ(), 60, 4.0D, 2.0D, 4.0D, 0.1D);
        server.playSound(null, this.blockPosition(), ModSounds.ANTI_PULSE.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
    }

    /** Salto del vacio: aparece junto a su objetivo. */
    private void voidStep(LivingEntity target) {
        double x = target.getX() + (this.random.nextDouble() - 0.5D) * 8.0D;
        double z = target.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D;
        this.randomTeleport(x, target.getY(), z, true);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.5F, 1.4F);
    }

    // ---- barra de jefe: solo para quien le pega
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && source.getEntity() instanceof ServerPlayer sp) {
            this.bossEvent.addPlayer(sp);
        }
        return result;
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    // ---- primera victoria: Alma de Ade
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level().isClientSide()) {
            return;
        }
        this.bossEvent.removeAllPlayers();
        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(64.0D))) {
            CompoundTag root = p.getPersistentData();
            CompoundTag persisted = root.getCompound(PERSISTED);
            if (!persisted.getBoolean(ADE_GIVEN)) {
                persisted.putBoolean(ADE_GIVEN, true);
                root.put(PERSISTED, persisted);
                ItemStack soul = new ItemStack(ModItems.ADE_SOUL.get());
                if (!p.getInventory().add(soul)) {
                    p.drop(soul, false);
                }
                p.displayClientMessage(Component.translatable("message.cosmog.ade_awakened"), false);
            }
        }
    }

    // ---- reglas de jefe
    @Override
    public void checkDespawn() {
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.enraged);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.enraged = tag.getBoolean("Enraged");
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ANTI_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ANTI_DEATH.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ANTI_AMBIENT.get();
    }
}
