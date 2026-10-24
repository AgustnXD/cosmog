package com.cosmog.entity;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * El Regulador de Cosmos: unico enemigo de The Cosmog.
 * - 600 de vida, armadura 14, inmune al fuego, no retrocede.
 * - Pulso cosmico (dano en area + empuje), atraccion gravitatoria y teletransporte hacia su objetivo.
 * - Fase 2 (<50% vida): mas rapido y con pulsos mas fuertes y frecuentes.
 */
public class CosmosRegulatorEntity extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.cosmog.cosmos_regulator"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);

    private int pulseCooldown = 100;
    private int pullCooldown = 220;
    private int teleportCooldown = 160;
    private boolean enraged = false;

    public CosmosRegulatorEntity(EntityType<? extends Monster> type, Level level) {
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
                .add(Attributes.FOLLOW_RANGE, 18.0D);
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

        if (!this.enraged && this.getHealth() < this.getMaxHealth() * 0.5F) {
            this.enraged = true;
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
            this.level().playSound(null, this.blockPosition(), ModSounds.REGULATOR_PULSE.get(), SoundSource.HOSTILE, 3.0F, 0.7F);
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        if (--this.pulseCooldown <= 0) {
            this.cosmicPulse();
            this.pulseCooldown = this.enraged ? 70 : 130;
        }
        if (--this.pullCooldown <= 0) {
            if (this.distanceToSqr(target) > 64.0D) {
                this.gravityPull(target);
            }
            this.pullCooldown = this.enraged ? 120 : 200;
        }
        if (--this.teleportCooldown <= 0) {
            if (this.distanceToSqr(target) > 400.0D) {
                this.teleportNear(target);
            }
            this.teleportCooldown = 160;
        }
    }

    /** Onda expansiva cosmica: dano magico en area y empuje. */
    private void cosmicPulse() {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        float damage = this.enraged ? 16.0F : 10.0F;
        List<Player> players = server.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(11.0D, 4.0D, 11.0D),
                p -> p.isAlive() && !p.isSpectator() && !p.isCreative());
        for (Player p : players) {
            p.hurt(this.damageSources().magic(), damage);
            Vec3 away = p.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
            if (away.lengthSqr() > 1.0E-4D) {
                away = away.normalize();
            }
            p.push(away.x * 1.4D, 0.55D, away.z * 1.4D);
            p.hurtMarked = true;
        }
        server.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0D, this.getZ(), 120, 5.0D, 1.0D, 5.0D, 0.15D);
        server.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 2.0D, this.getZ(), 80, 4.0D, 2.0D, 4.0D, 0.2D);
        server.playSound(null, this.blockPosition(), ModSounds.REGULATOR_PULSE.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
    }

    /** Atrae al objetivo lejano hacia el jefe. */
    private void gravityPull(LivingEntity target) {
        Vec3 dir = this.position().subtract(target.position());
        if (dir.lengthSqr() < 1.0E-4D) {
            return;
        }
        dir = dir.normalize();
        target.setDeltaMovement(dir.x * 1.6D, 0.4D, dir.z * 1.6D);
        target.hurtMarked = true;
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.PORTAL, target.getX(), target.getY() + 1.0D, target.getZ(), 60, 0.5D, 1.0D, 0.5D, 0.5D);
            server.playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 0.6F);
        }
    }

    private void teleportNear(LivingEntity target) {
        double x = target.getX() + (this.random.nextDouble() - 0.5D) * 10.0D;
        double z = target.getZ() + (this.random.nextDouble() - 0.5D) * 10.0D;
        this.randomTeleport(x, target.getY(), z, true);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.5F, 0.5F);
    }

    // ---- barra de jefe ------------------------------------------------------
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    // ---- reglas de jefe -----------------------------------------------------
    @Override
    public void checkDespawn() {
        // nunca desaparece (tampoco en Pacifico)
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
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    // ---- sonidos --------------------------------------------------------------
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.REGULATOR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REGULATOR_DEATH.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.REGULATOR_AMBIENT.get();
    }
}
