package com.cosmog.entity;

import com.cosmog.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Stalker: solo ataca cuando le das la espalda. Si lo miras antes de que te golpee, huye corriendo y desaparece.
 */
public class StalkerEntity extends Monster {
    private int fleeTicks = 0;
    private int fleeRepath = 0;
    private int attackCooldown = 0;

    public StalkerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static boolean checkSpawn(EntityType<StalkerEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                     BlockPos pos, RandomSource random) {
        return Mob.checkMobSpawnRules(type, level, reason, pos, random) && random.nextInt(5) == 0;
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
        return look.dot(toMe) > 0.45D && player.hasLineOfSight(this) && this.distanceToSqr(player) < 40.0D * 40.0D;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        LivingEntity target = this.getTarget();

        if (this.fleeTicks > 0) {   // huyendo: desaparece al terminar
            this.fleeTicks--;
            if (target != null && --this.fleeRepath <= 0) {
                Vec3 away = DefaultRandomPos.getPosAway(this, 24, 10, target.position());
                if (away != null) {
                    this.getNavigation().moveTo(away.x, away.y, away.z, 1.7D);
                }
                this.fleeRepath = 15;
            }
            if (this.fleeTicks == 0) {
                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0D, this.getZ(), 30, 0.3D, 0.8D, 0.3D, 0.02D);
                }
                this.discard();
            }
            return;
        }

        if (!(target instanceof Player player) || !player.isAlive()) {
            return;
        }
        if (this.isSeenBy(player)) {
            this.fleeTicks = 100;
            this.fleeRepath = 0;
            return;
        }
        this.getNavigation().moveTo(player, 1.25D);   // se acerca mientras no lo mires
        if (this.distanceToSqr(player) < 3.0D && this.attackCooldown <= 0) {
            player.hurt(this.damageSources().mobAttack(this), 12.0F);
            this.attackCooldown = 40;
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
        return SoundEvents.PHANTOM_DEATH;
    }
}
