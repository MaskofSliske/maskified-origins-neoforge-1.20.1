package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginRegenEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins", "origin"));

    private static final Set<ResourceLocation> REGEN_ORIGINS = Set.of(
            new ResourceLocation("maskifiedorigins", "abyssal_dragon"),
            new ResourceLocation("maskifiedorigins", "avali")
    );

    @SubscribeEvent
    @SuppressWarnings("resource")
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        boolean hasOrigin = hasRegenOrigin(player);
        boolean hasEffect = player.hasEffect(MobEffects.REGENERATION);
        System.out.println("hasOrigin=" + hasOrigin + " hasEffect=" + hasEffect);

        if (hasOrigin && !hasEffect) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 0, true, false, true));
        } else if (!hasOrigin && hasEffect) {
            player.removeEffect(MobEffects.REGENERATION);
        }
    }

    private static boolean hasRegenOrigin(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return REGEN_ORIGINS.contains(origin.location());
        }).orElse(false);
    }
}