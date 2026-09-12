package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginAmmoniaPhysiologyEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins","origin"));

    private static final ResourceLocation AVALI = new ResourceLocation("maskifiedorigins","avali");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SuppressWarnings("resource")
    public static void onEffectApplication(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasAmmoniaPhysiology(player)) return;

        if (event.getEffectInstance().getEffect() == MobEffects.REGENERATION && event.getEffectInstance().getDuration() == -1) {return;}

        if (event.getEffectInstance().getEffect().getCategory() == MobEffectCategory.BENEFICIAL) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("resource")
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!isAvali(player)) return;
        if (!event.getItem().is(net.minecraft.world.item.Items.SUSPICIOUS_STEW)) return;

        event.setCanceled(true);
    }

    private static boolean isAvali(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(new ResourceLocation("maskifiedorigins", "avali"));
        }).orElse(false);
    }

    static boolean hasAmmoniaPhysiology(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);

        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(AVALI);
        }).orElse(false);
    }
}
