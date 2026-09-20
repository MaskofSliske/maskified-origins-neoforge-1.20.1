package com.example.maskifiedorigins.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class DroneEntity extends TamableAnimal implements FlyingAnimal, RangedAttackMob {

    private static final EntityDataAccessor<Boolean> DATA_RESTING =
            SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.BOOLEAN);

    private static final double IDLE_RANGE = 8.0D;
    private static final int IDLE_TICKS_TO_REST = 12000;
    private static final double FOLLOW_RANGE = 32.0D;
    private static final double HOVER_HEIGHT = 1.5D;

    private int idleTicks = 0;

    public final AnimationState flyingAnimationState = new AnimationState();
    public final AnimationState landingGearAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    public DroneEntity(EntityType<? extends DroneEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_RESTING, false);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
            }
        };
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DroneShootGoal(this));
        this.goalSelector.addGoal(2, new HoverNearOwnerGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    @SuppressWarnings("resource")
    public void performRangedAttack(LivingEntity target, float velocity) {
        Arrow arrow = new Arrow(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.33D) - arrow.getY();
        double dz = target.getZ() - this.getZ();
        arrow.shoot(dx, dy + Math.sqrt(dx * dx + dz * dz) * 0.2D, dz, 1.6F, 8.0F);
        this.level().addFreshEntity(arrow);
    }

    private static class DroneShootGoal extends Goal {
        private final DroneEntity drone;
        private int cooldown = 10;

        DroneShootGoal(DroneEntity drone) {
            this.drone = drone;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = this.drone.getTarget();
            return t != null && t.isAlive() && !this.drone.isOrderedToSit();
        }

        @Override
        public void tick() {
            LivingEntity t = this.drone.getTarget();
            if (t == null) return;
            this.drone.getLookControl().setLookAt(t, 30.0F, 30.0F);
            if (--this.cooldown <= 0
                    && this.drone.distanceToSqr(t) <= 256.0D
                    && this.drone.hasLineOfSight(t)) {
                this.drone.performRangedAttack(t, 1.0F);
                this.cooldown = 20;
            }
        }
    }

    private static class HoverNearOwnerGoal extends Goal {
        private final DroneEntity drone;

        HoverNearOwnerGoal(DroneEntity drone) {
            this.drone = drone;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.drone.isOrderedToSit() && this.drone.getOwner() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity owner = this.drone.getOwner();
            if (owner == null) return;
            double dist = this.drone.distanceToSqr(owner);
            if (dist > 100.0D) {
                this.drone.getNavigation().moveTo(owner.getX(), owner.getY() + HOVER_HEIGHT, owner.getZ(), 1.0D);
            } else {
                Vec3 target = new Vec3(owner.getX() + 1.0, owner.getY() + HOVER_HEIGHT, owner.getZ() + 1.0);
                this.drone.getMoveControl().setWantedPosition(target.x, target.y, target.z, 0.5D);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && player.isShiftKeyDown() && player.equals(this.getOwner())) {
            boolean sitting = !this.isOrderedToSit();
            this.setOrderedToSit(sitting);
            if (sitting) {
                this.setResting(false);
                this.setTarget(null);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = this.getOwner();
        if (owner != null && !this.level().isClientSide()) {
            this.setYRot(owner.getYRot());
        }
        this.yBodyRot = this.getYRot();
        if (this.level().isClientSide()) {
            this.tickAnimationStates();
        } else {
            this.tickIdleRestLogic();
        }
    }

    private void tickIdleRestLogic() {
        if (this.isOrderedToSit()) {
            this.idleTicks = 0;
            return;
        }

        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        if (this.isResting()) {
            if (this.getTarget() != null) {
                this.setResting(false);
            }
        } else {
            boolean idleEligible = this.getTarget() == null && this.distanceToSqr(owner) <= (IDLE_RANGE * IDLE_RANGE);
            this.idleTicks = idleEligible ? this.idleTicks + 1 : 0;
            if (this.idleTicks >= IDLE_TICKS_TO_REST) {
                this.setResting(true);
                this.idleTicks = 0;
                this.setTarget(null);
            }
        }
    }

    private void tickAnimationStates() {
        boolean sitting = this.isOrderedToSit();
        boolean gearDeployed = sitting || this.isResting() || this.isDeadOrDying();

        this.deathAnimationState.animateWhen(this.isDeadOrDying(), this.tickCount);
        this.landingGearAnimationState.animateWhen(gearDeployed, this.tickCount);
        this.flyingAnimationState.animateWhen(!sitting && !this.isDeadOrDying(), this.tickCount);
    }

    public boolean isResting() {
        return this.entityData.get(DATA_RESTING);
    }

    public void setResting(boolean resting) {
        this.entityData.set(DATA_RESTING, resting);
    }

    public boolean rotorsSpinning() {
        return this.flyingAnimationState.isStarted() && !this.deathAnimationState.isStarted();
    }

    public void bindOwner(Player owner) {
        this.setOwnerUUID(owner.getUUID());
        this.setPersistenceRequired();
    }

    public static DroneEntity summon(ServerLevel level, Player owner, EntityType<DroneEntity> type) {
        List<DroneEntity> existing = level.getEntitiesOfClass(DroneEntity.class,
                owner.getBoundingBox().inflate(64.0D), t -> owner.getUUID().equals(t.getOwnerUUID()));
        existing.forEach(Entity::discard);

        DroneEntity drone = type.create(level);
        if (drone == null) return null;

        drone.moveTo(owner.getX(), owner.getY() + HOVER_HEIGHT, owner.getZ(), owner.getYRot(), 0);
        drone.setOwnerUUID(owner.getUUID());
        drone.setTame(true);
        drone.setPersistenceRequired();
        level.addFreshEntity(drone);
        return drone;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return null;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
}