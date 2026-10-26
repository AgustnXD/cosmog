package com.cosmog.entity;

import com.cosmog.registry.ModDimensions;
import com.cosmog.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * True Ade: companera humana. Sigue, se sienta, pelea, habla y se encarina.
 *  - Manzanas doradas: +10 de vida maxima (hasta triplicarla). Manzanas de Notch: +fuerza (hasta triplicarla) y
 *    reducen a la mitad el dano de todo menos fuego y caida.
 *  - Regalos: aumentan el afecto (0-100). Aprende tus regalos favoritos (se aburre de lo repetido) y recuerda que mobs la hieren
 *    (les pega mas fuerte con el tiempo).
 *  - Dialogos segun el contexto y el afecto.
 */
public class TrueAdeEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> AFFECTION = SynchedEntityData.defineId(TrueAdeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HP_STEPS = SynchedEntityData.defineId(TrueAdeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STR_STEPS = SynchedEntityData.defineId(TrueAdeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RESISTANT = SynchedEntityData.defineId(TrueAdeEntity.class, EntityDataSerializers.BOOLEAN);

    public static final int MAX_HP_STEPS = 12;
    public static final int MAX_STR_STEPS = 6;
    private static final double BASE_HP = 60.0D;
    private static final double BASE_DMG = 14.0D;

    private final Map<String, Integer> gifts = new HashMap<>();
    private final Map<String, Integer> grudges = new HashMap<>();
    private int lastSpeak = -1000;
    private int idleTimer = 2400;
    private boolean greeted = false;
    private ResourceKey<Level> lastDim = null;
    private LivingEntity lastTarget = null;
    private float lastOwnerHealth = -1.0F;

    public TrueAdeEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HP)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, BASE_DMG)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AFFECTION, 0);
        this.entityData.define(HP_STEPS, 0);
        this.entityData.define(STR_STEPS, 0);
        this.entityData.define(RESISTANT, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.25D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, e -> e instanceof Enemy));
    }

    // ------------------------------------------------------------------ estadisticas
    public int getAffection() { return this.entityData.get(AFFECTION); }
    public int getHpSteps() { return this.entityData.get(HP_STEPS); }
    public int getStrSteps() { return this.entityData.get(STR_STEPS); }
    public boolean isResistant() { return this.entityData.get(RESISTANT); }

    private void applyStats() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP + 10.0D * this.getHpSteps());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DMG + (BASE_DMG * 2.0D / MAX_STR_STEPS) * this.getStrSteps());
    }

    public CompoundTag saveStats() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Affection", this.getAffection());
        tag.putInt("HpSteps", this.getHpSteps());
        tag.putInt("StrSteps", this.getStrSteps());
        tag.putBoolean("Resistant", this.isResistant());
        CompoundTag g = new CompoundTag();
        this.gifts.forEach(g::putInt);
        tag.put("Gifts", g);
        CompoundTag gr = new CompoundTag();
        this.grudges.forEach(gr::putInt);
        tag.put("Grudges", gr);
        return tag;
    }

    public void loadStats(CompoundTag tag) {
        this.entityData.set(AFFECTION, tag.getInt("Affection"));
        this.entityData.set(HP_STEPS, Math.min(MAX_HP_STEPS, tag.getInt("HpSteps")));
        this.entityData.set(STR_STEPS, Math.min(MAX_STR_STEPS, tag.getInt("StrSteps")));
        this.entityData.set(RESISTANT, tag.getBoolean("Resistant"));
        this.gifts.clear();
        CompoundTag g = tag.getCompound("Gifts");
        for (String k : g.getAllKeys()) {
            this.gifts.put(k, g.getInt(k));
        }
        this.grudges.clear();
        CompoundTag gr = tag.getCompound("Grudges");
        for (String k : gr.getAllKeys()) {
            this.grudges.put(k, gr.getInt(k));
        }
        this.applyStats();
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("AdeData", this.saveStats());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AdeData")) {
            float hp = this.getHealth();
            this.loadStats(tag.getCompound("AdeData"));
            if (tag.contains("Health")) {
                this.setHealth(Math.min(this.getMaxHealth(), tag.getFloat("Health")));
            } else {
                this.setHealth(Math.min(this.getMaxHealth(), hp));
            }
        }
    }

    // ------------------------------------------------------------------ dialogos
    private void say(String context) {
        this.say(context, false);
    }

    private void say(String context, boolean force) {
        if (this.level().isClientSide()) {
            return;
        }
        if (!force && this.tickCount - this.lastSpeak < 200) {
            return;
        }
        if (!(this.getOwner() instanceof ServerPlayer owner) || this.distanceToSqr(owner) > 48.0D * 48.0D) {
            return;
        }
        this.lastSpeak = this.tickCount;
        Component line = Component.translatable("dialogue.cosmog.ade." + context + "." + (1 + this.random.nextInt(3)));
        owner.sendSystemMessage(Component.literal("<Ade> ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(line.copy().withStyle(ChatFormatting.WHITE)));
    }

    private void hearts() {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.8D, this.getZ(), 6, 0.4D, 0.3D, 0.4D, 0.02D);
        }
    }

    // ------------------------------------------------------------------ interaccion
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isOwnedBy(player) || hand != InteractionHand.MAIN_HAND) {
            return super.mobInteract(player, hand);
        }
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        if (item == Items.GOLDEN_APPLE) {
            if (this.getHpSteps() >= MAX_HP_STEPS) {
                this.say("golden_max", true);
                return InteractionResult.CONSUME;
            }
            this.entityData.set(HP_STEPS, this.getHpSteps() + 1);
            this.applyStats();
            this.setHealth(this.getMaxHealth());
            this.consume(player, stack);
            this.hearts();
            this.playSound(SoundEvents.PLAYER_LEVELUP, 0.6F, 1.4F);
            this.say("golden", true);
            return InteractionResult.CONSUME;
        }
        if (item == Items.ENCHANTED_GOLDEN_APPLE) {
            if (this.getStrSteps() >= MAX_STR_STEPS) {
                this.say("notch_max", true);
                return InteractionResult.CONSUME;
            }
            this.entityData.set(STR_STEPS, this.getStrSteps() + 1);
            this.entityData.set(RESISTANT, true);
            this.applyStats();
            this.setHealth(this.getMaxHealth());
            this.consume(player, stack);
            this.hearts();
            this.playSound(SoundEvents.PLAYER_LEVELUP, 0.8F, 0.8F);
            this.say("notch", true);
            return InteractionResult.CONSUME;
        }
        if (!stack.isEmpty()) {
            int points = this.giftValue(stack);
            if (points > 0) {
                this.receiveGift(player, stack, points);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {   // estado
            player.displayClientMessage(Component.translatable("message.cosmog.ade_status", this.getAffection(), this.getHpSteps(),
                    this.getStrSteps(), this.isResistant() ? Component.translatable("message.cosmog.ade_resist") : Component.literal("")), false);
            return InteractionResult.CONSUME;
        }
        boolean sit = !this.isOrderedToSit();
        this.setOrderedToSit(sit);
        this.setInSittingPose(sit);
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);
        this.say(sit ? "sit" : "follow", true);
        return InteractionResult.CONSUME;
    }

    private void consume(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private int giftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (stack.is(ItemTags.FLOWERS)) return 3;
        if (item == Items.CAKE) return 8;
        if (item == Items.COOKIE) return 4;
        if (item == Items.PUMPKIN_PIE) return 5;
        if (item == Items.SWEET_BERRIES || item == Items.GLOW_BERRIES) return 3;
        if (item == Items.HONEY_BOTTLE) return 5;
        if (item == Items.DIAMOND) return 10;
        if (item == Items.EMERALD) return 7;
        if (item == Items.AMETHYST_SHARD) return 4;
        if (item == Items.PINK_DYE) return 3;
        if (item == Items.GOLDEN_CARROT) return 5;
        if (item == Items.APPLE) return 2;
        if (item == Items.BREAD || item == Items.COOKED_CHICKEN) return 1;
        if (stack.isDamageableItem() || stack.getMaxStackSize() == 1) return 0;   // no acepta herramientas ni armas
        return 1;                                                                  // curiosidad por cosas nuevas
    }

    private void receiveGift(Player player, ItemStack stack, int base) {
        String key = String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));
        int count = this.gifts.getOrDefault(key, 0);
        double novelty = count == 0 ? 1.5D : 1.0D / (1.0D + 0.35D * count);   // aprende: lo repetido le importa menos
        int points = Math.max(1, (int) Math.round(base * novelty));
        int before = this.getAffection();
        int after = Math.min(100, before + points);
        this.entityData.set(AFFECTION, after);
        this.gifts.put(key, count + 1);
        this.consume(player, stack);
        this.hearts();
        this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.5F);

        String favorite = null;
        int best = 2;
        for (Map.Entry<String, Integer> e : this.gifts.entrySet()) {
            if (e.getValue() > best) {
                best = e.getValue();
                favorite = e.getKey();
            }
        }
        if (before < 25 && after >= 25) this.say("tier25", true);
        else if (before < 50 && after >= 50) this.say("tier50", true);
        else if (before < 75 && after >= 75) this.say("tier75", true);
        else if (key.equals(favorite)) this.say("gift_fav", true);
        else if (count >= 4) this.say("gift_bored", true);
        else this.say("gift", true);
    }

    // ------------------------------------------------------------------ combate
    @Override
    public boolean hurt(DamageSource source, float amount) {
        float finalAmount = amount;
        if (this.isResistant() && !source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypeTags.IS_FALL)) {
            finalAmount *= 0.5F;   // resistencias duplicadas (salvo fuego y caida)
        }
        if (source.getEntity() instanceof Mob attacker && !(attacker instanceof TamableAnimal)) {
            String key = String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()));
            this.grudges.merge(key, 1, (a, b) -> Math.min(20, a + b));
        }
        boolean result = super.hurt(source, finalAmount);
        if (result && !this.level().isClientSide()) {
            this.say(this.getHealth() < this.getMaxHealth() * 0.3F ? "lowhp" : "hurt");
        }
        return result;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float multiplier = 1.0F + (this.getAffection() >= 50 ? 0.2F : 0.0F);
        if (target instanceof Mob mob) {   // aprende de sus enemigos
            String key = String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()));
            multiplier += Math.min(0.5F, 0.05F * this.grudges.getOrDefault(key, 0));
        }
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        boolean hit = target.hurt(this.damageSources().mobAttack(this), damage);
        if (hit && target instanceof LivingEntity living) {
            living.knockback(0.6D, this.getX() - living.getX(), this.getZ() - living.getZ());
        }
        return hit;
    }

    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity entity) {
        boolean result = super.killedEntity(level, entity);
        this.say("kill");
        return result;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide()) {   // vuelve como item con sus recuerdos
            ItemStack soul = new ItemStack(ModItems.TRUE_ADE_SOUL.get());
            soul.getOrCreateTag().put("AdeData", this.saveStats());
            this.spawnAtLocation(soul);
        }
    }

    // ------------------------------------------------------------------ comportamiento
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity owner = this.getOwner();

        if (!this.greeted && this.tickCount > 20) {
            this.greeted = true;
            this.say("summon", true);
        }
        ResourceKey<Level> dim = this.level().dimension();
        if (this.lastDim != dim) {
            if (this.lastDim != null) {
                this.say(dim == ModDimensions.UMBRA ? "umbra" : (dim == ModDimensions.COSMOG ? "cosmog" : "overworld"), true);
            }
            this.lastDim = dim;
        }
        LivingEntity target = this.getTarget();
        if (target != null && this.lastTarget == null && target instanceof Enemy) {
            this.say("combat");
        }
        this.lastTarget = target;

        if (owner != null) {
            float h = owner.getHealth();
            if (this.lastOwnerHealth >= 0.0F && this.lastOwnerHealth - h >= 4.0F) {
                this.say("ownerhurt");
            }
            this.lastOwnerHealth = h;
            int aff = this.getAffection();
            if (this.tickCount % 40 == 0 && aff >= 25 && this.distanceToSqr(owner) < 64.0D && owner.getHealth() < owner.getMaxHealth()) {
                owner.heal(aff >= 75 ? 2.0F : 1.0F);   // el afecto la hace cuidarte
                this.hearts();
            }
        }
        if (--this.idleTimer <= 0) {
            int aff = this.getAffection();
            this.say(aff >= 60 ? "idle_high" : (aff >= 25 ? "idle_mid" : "idle_low"));
            this.idleTimer = 3600 + this.random.nextInt(3600);
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }
}
