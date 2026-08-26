package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginSizeEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER =
            ResourceKey.create(OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins","origin"));

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isTropicalDragon(player)) return;

        if (event.getPose() == Pose.SWIMMING) {
            event.setNewSize(EntityDimensions.scalable(1.0f,1.5f));
        } else {
            event.setNewSize(EntityDimensions.scalable(1.0f,2.0f));
        }
    }

    private static boolean isTropicalDragon(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(new ResourceLocation("maskifiedorigins", "tropical_storm_dragon"));
        }).orElse(false);
    }
}