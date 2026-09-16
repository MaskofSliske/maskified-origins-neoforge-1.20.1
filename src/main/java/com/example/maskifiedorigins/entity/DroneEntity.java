package com.example.maskifiedorigins.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class DroneEntity extends TamableAnimal implements FlyingAnimal {

    private static final double FOLLOW_RANGE = 16.0D;
    private static final double HOVER_HEIGHT = 1.5D;

    public DroneEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new MoveControl(this) {
            @Override
            public void tick() {
                if (DroneEntity.this.getOwner() == null) return;
                //gonna try to utilize HoverGoal
            }
        };
        this.setNoGravity(true);
        this.noPhysics = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new HoverNearOwnerGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false,
                target -> target.distanceToSqr(this) <= FOLLOW_RANGE * FOLLOW_RANGE));
    }

    public static DroneEntity summon(ServerLevel level, Player owner, EntityType<DroneEntity> type) {
        List<DroneEntity> existing = level.getEntitiesOfClass(DroneEntity.class, owner.getBoundingBox().inflate(64.0D), t -> owner.getUUID().equals(t.getOwnerUUID()));
        existing.forEach(Entity::discard);
        //making sure to remove existing drones when required
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

    //trying to set a simply HoverGoal to keep the drone aloft
    private static class HoverNearOwnerGoal extends Goal {
        private final DroneEntity drone;

        HoverNearOwnerGoal(DroneEntity drone) {
            this.drone = drone;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return drone.getOwner() !=null;
        }

        @Override
        public void tick() {
            LivingEntity owner = drone.getOwner();
            if (owner == null) return;
            double dist = drone.distanceToSqr(owner);
            if (dist > 100.0D) {
                drone.getNavigation().moveTo(owner.getX(), owner.getY() + HOVER_HEIGHT, owner.getZ(), 1.0D);
            } else {
                Vec3 target = new Vec3(owner.getX() + 1.0, owner.getY() + HOVER_HEIGHT, owner.getZ() + 1.0);
                drone.getMoveControl().setWantedPosition(target.x, target.y, target.z, 0.5D);
            }
        }

    }
}
