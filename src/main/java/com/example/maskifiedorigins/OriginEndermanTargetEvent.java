package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginEndermanTargetEvent {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins","origin"));

    private static final ResourceLocation ABYSSAL_DRAGON = new ResourceLocation("maskifiedorigins","abyssal_dragon");

    @SubscribeEvent
    public static void onEndermanTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof EnderMan enderman)) return;

        LivingEntity newTarget = event.getNewTarget();
        if (!(newTarget instanceof Player player)) return;
        if (!isAbyssalDragon(player)) return;

        //then ofc allowing retaliation if the enderman is attacked because they should be able to attempt to defend themselves against what is basically a void tiger
        if (enderman.getLastHurtByMob() == player) return;

        event.setCanceled(true);
    }

    private static boolean isAbyssalDragon(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(ABYSSAL_DRAGON);
                }).orElse(false);
    }
}
