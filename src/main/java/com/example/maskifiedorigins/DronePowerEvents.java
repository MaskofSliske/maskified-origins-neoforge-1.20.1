package com.example.maskifiedorigins;

import com.example.maskifiedorigins.entity.custom.DroneEntity;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class DronePowerEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins", "origin"));
    private static final ResourceLocation AVALI = new ResourceLocation("maskifiedorigins", "avali");

    public static boolean isAvali(ServerPlayer player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(AVALI);
        }).orElse(false);
    }

    // Cleanup on death
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        removeDrone(player);
    }

    // Cleanup on logout
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        removeDrone(player);
    }

    private static void removeDrone(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        List<DroneEntity> existing = level.getEntitiesOfClass(
                DroneEntity.class,
                player.getBoundingBox().inflate(128.0D),
                t -> player.getUUID().equals(t.getOwnerUUID()));
        existing.forEach(net.minecraft.world.entity.Entity::discard);
    }
}