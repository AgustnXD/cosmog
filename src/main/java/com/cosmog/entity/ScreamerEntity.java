package com.cosmog.entity;

import com.cosmog.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Screamer: aparece gritando, corre hacia ti, te golpea una vez y desaparece. */
public class ScreamerEntity extends Monster {
    private int life = 0;

    public ScreamerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    public static boolean checkSpawn(EntityType<ScreamerEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                     BlockPos pos, RandomSource random) {
        return Mob.checkMobSpawnRules(type, level, reason, pos, random) && random.nextInt(6) == 0;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.tickCount == 1) {
            this.playSound(ModSounds.SCREAMER_SCREAM.get(), 2.0F, 1.0F);   // grito al aparecer
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (++this.life > 400) {   // si no te alcanza, se esfuma
            this.discard();
            return;
        }
        LivingEntity target = this.getTarget();
        if (!(target instanceof Player player) || !player.isAlive()) {
            return;
        }
        this.getNavigation().moveTo(player, 1.7D);
        if (this.distanceToSqr(player) < 3.2D) {   // un golpe y se va
            player.hurt(this.damageSources().mobAttack(this), 6.0F);   // 3 corazones sin armadura
            player.knockback(1.0D, this.getX() - player.getX(), this.getZ() - player.getZ());
            player.hurtMarked = true;
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
            this.level().playSound(null, this.blockPosition(), ModSounds.SCREAMER_SCREAM.get(), SoundSource.HOSTILE, 2.0F, 1.3F);
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0D, this.getZ(), 25, 0.3D, 0.8D, 0.3D, 0.02D);
            }
            this.discard();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }
}
