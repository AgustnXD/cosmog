package com.cosmog.entity;

import com.cosmog.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * El Acechador: criatura alta y esqueletica, muy fuerte (como un "dweller"), pero le aterra la mirada.
 * - Te sigue solo mientras NO lo miras. Al acercarse: susurros y pisadas pesadas.
 * - Si lo miras, huye corriendo y desaparece. Si te alcanza de espaldas, golpea con muchisima fuerza.
 */
public class StalkerEntity extends Monster {
    private int fleeTicks = 0;
    private int fleeRepath = 0;
    private int attackCooldown = 0;
    private int stepCooldown = 0;
    private int whisperCooldown = 60;

    public StalkerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    private boolean isSeenBy(Player player) {
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 toMe = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ()).normalize();
        return look.dot(toMe) > 0.55D && player.hasLineOfSight(this) && this.distanceToSqr(player) < 40.0D * 40.0D;
    }

    public boolean isFleeing() {
        return this.fleeTicks > 0;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        LivingEntity target = this.getTarget();

        if (this.fleeTicks > 0) {   // huye y desaparece
            this.fleeTicks--;
            if (target != null && --this.fleeRepath <= 0) {
                Vec3 away = DefaultRandomPos.getPosAway(this, 24, 10, target.position());
                if (away != null) {
                    this.getNavigation().moveTo(away.x, away.y, away.z, 1.9D);
                }
                this.fleeRepath = 12;
            }
            if (this.fleeTicks == 0) {
                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.5D, this.getZ(), 40, 0.4D, 1.2D, 0.4D, 0.02D);
                }
                this.discard();
            }
            return;
        }

        if (!(target instanceof Player player) || !player.isAlive()) {
            return;
        }
        double distance = Math.sqrt(this.distanceToSqr(player));

        if (this.isSeenBy(player)) {   // la mirada le da miedo
            this.fleeTicks = 110;
            this.fleeRepath = 0;
            this.playSound(ModSounds.STALKER_AMBIENT.get(), 1.4F, 0.6F);
            return;
        }

        this.getNavigation().moveTo(player, distance < 12.0D ? 1.65D : 1.35D);   // se acerca mientras no lo mires

        // anuncia su llegada: susurros y pisadas fuertes
        if (distance < 32.0D && --this.whisperCooldown <= 0) {
            this.playSound(ModSounds.STALKER_WHISPER.get(), 1.3F, 0.85F + this.random.nextFloat() * 0.3F);
            this.whisperCooldown = 90 + this.random.nextInt(120);
        }
        if (distance < 30.0D && this.onGround() && this.getDeltaMovement().horizontalDistanceSqr() > 0.0004D && --this.stepCooldown <= 0) {
            this.playSound(ModSounds.STALKER_STEP.get(), (float) Math.max(0.5D, 2.0D - distance / 18.0D), 0.8F + this.random.nextFloat() * 0.3F);
            this.stepCooldown = distance < 10.0D ? 7 : 12;
        }

        if (distance < 3.6D && this.attackCooldown <= 0) {
            if (player.hurt(this.damageSources().mobAttack(this), 16.0F)) {
                player.knockback(1.6D, this.getX() - player.getX(), this.getZ() - player.getZ());
                player.hurtMarked = true;
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
            }
            this.attackCooldown = 60;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.STALKER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }
}
