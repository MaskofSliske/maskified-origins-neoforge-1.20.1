package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginFlammableEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins","origin"));

    private static final Set<ResourceLocation> FLAMMABLE_ORIGINS = Set.of(
            new ResourceLocation("maskifiedorigins","abyssal_dragon"),
            new ResourceLocation("maskifiedorigins","avali")
    );

    private static final float FIRE_DAMAGE_MULTIPLIER = 2.0f;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        DamageSource source = event.getSource();
        boolean isFireDamage = source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.FIREBALL);
        if (!isFireDamage) return;

        if (!hasFlammableOrigin(player)) return;

        event.setAmount(event.getAmount() * FIRE_DAMAGE_MULTIPLIER);
    }

    private static boolean hasFlammableOrigin(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return FLAMMABLE_ORIGINS.contains(origin.location());
        }).orElse(false);
    }
}
