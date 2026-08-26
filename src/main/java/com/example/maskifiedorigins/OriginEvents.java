package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins", "origin"));

    @SubscribeEvent
    @SuppressWarnings("resource")
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        boolean isTropicalDragon = containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(new ResourceLocation("maskifiedorigins", "tropical_storm_dragon"));
        }).orElse(false);

        if (isTropicalDragon) {
            //making it tick down half as fast, cause it broke when I tried to simply double it lmao
            if (player.isUnderWater() && player.getAirSupply() > 0 && player.tickCount % 2 == 0) {
                player.setAirSupply(Math.min(player.getAirSupply() + 1, player.getMaxAirSupply()));
            }
        }
    }
}