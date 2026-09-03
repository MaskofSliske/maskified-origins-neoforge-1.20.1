package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginScaryEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins", "origin"));

    private static final Set<ResourceLocation> SCARY_ORIGINS = new HashSet<>(List.of(
            new ResourceLocation("maskifiedorigins", "tropical_storm_dragon"),
            new ResourceLocation("maskifiedorigins","abyssal_dragon")
    ));

    private static final double RADIUS = 16.0;
    private static final int SCAN_INTERVAL_TICKS = 40; // every 2 seconds

    // Tracks which mobs currently have goals applied, and because of which player,
    // so goals get cleaned up if that specific player leaves range or loses the origin.
    private static final Map<Mob, AvoidEntityGoal<Player>> ACTIVE_AVOID_GOALS = new HashMap<>();
    private static final Map<Mob, Player> AFFECTED_BY = new HashMap<>();

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (++tickCounter % SCAN_INTERVAL_TICKS != 0) return;

        for (var level : net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            for (Player player : level.players()) {
                if (!isScary(player)) continue;

                AABB area = player.getBoundingBox().inflate(RADIUS);
                List<Mob> nearby = level.getEntitiesOfClass(Mob.class, area);
                for (Mob mob : nearby) {
                    applyToEntity(mob, player);
                }
            }

            // Clean up mobs whose scary player is no longer in range or no longer scary
            List<Mob> toClear = new ArrayList<>();
            AFFECTED_BY.forEach((mob, player) -> {
                boolean stillValid = isScary(player) && mob.distanceTo(player) < RADIUS && mob.isAlive();
                if (!stillValid) toClear.add(mob);
            });
            toClear.forEach(OriginScaryEvents::removeFromEntity);
        }
    }

    private static boolean isScary(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return SCARY_ORIGINS.contains(origin.location());
        }).orElse(false);
    }

    public static void applyToEntity(Mob mob, Player scaryPlayer) {
        if (ACTIVE_AVOID_GOALS.containsKey(mob)) return; // already applied

        if (mob instanceof IronGolem golem) {
            if (golem.getTarget() == null) {
                golem.setTarget(scaryPlayer);
            }
            AFFECTED_BY.put(mob, scaryPlayer);
        } else if (mob instanceof Villager villager) {
            AvoidEntityGoal<Player> avoidGoal = new AvoidEntityGoal<>(
                    villager, Player.class, 10.0F, 1.0D, 1.2D
            );
            villager.goalSelector.addGoal(1, avoidGoal);
            ACTIVE_AVOID_GOALS.put(villager, avoidGoal);
            AFFECTED_BY.put(mob, scaryPlayer);
        } else if (mob instanceof Monster monster) {
            if (monster.getTarget() == scaryPlayer) {
                monster.setTarget(null);
            }
            AvoidEntityGoal<Player> avoidGoal = new AvoidEntityGoal<>(
                    monster, Player.class, 8.0F, 1.0D, 1.0D
            );
            monster.goalSelector.addGoal(1, avoidGoal);
            ACTIVE_AVOID_GOALS.put(monster, avoidGoal);
            AFFECTED_BY.put(mob, scaryPlayer);

            List<NearestAttackableTargetGoal<?>> toRemove = new ArrayList<>();
            monster.targetSelector.getRunningGoals().forEach(wrappedGoal -> {
                if (wrappedGoal.getGoal() instanceof NearestAttackableTargetGoal<?> targetGoal) {
                    toRemove.add(targetGoal);
                }
            });
            toRemove.forEach(monster.targetSelector::removeGoal);
        }
    }

    public static void removeFromEntity(LivingEntity entity) {
        if (!(entity instanceof Mob mob)) return;
        AvoidEntityGoal<Player> avoidGoal = ACTIVE_AVOID_GOALS.remove(mob);
        if (avoidGoal != null) {
            mob.goalSelector.removeGoal(avoidGoal);
        }
        AFFECTED_BY.remove(mob);
    }
}