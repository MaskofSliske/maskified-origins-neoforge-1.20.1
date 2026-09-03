package com.example.maskifiedorigins;


import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginAlienAnatomyEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins","origin"));

    private static final ResourceLocation ABYSSAL_DRAGON = new ResourceLocation("maskifiedorigins","abyssal_dragon");

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasAlienAnatomy(player)) return;

        if (player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasAlienAnatomy(player)) return;

        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasAlienAnatomy(player)) return;
        if (!isPotionInstantDamage(event.getSource(), player)) return;

        event.setCanceled(true);
    }

    private static boolean isPotionInstantDamage(DamageSource source, LivingEntity target) {
        if (!source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.INDIRECT_MAGIC)) return false;

        Entity direct = source.getDirectEntity();
        if (direct instanceof ThrownPotion) return true;
        if (direct instanceof AreaEffectCloud) return true;
        if (direct instanceof Arrow arrow) return true;
        if (direct == null) return true; //aka preventing non-entity sourced magic damage if it ever happens for some ungodly reason
        if (direct == target) return true; //then this is self-inflicted, with the instant damage potion or if someone has a modded in poisoning-via-food type deal
        return false;
    }

    static boolean hasAlienAnatomy(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(ABYSSAL_DRAGON);
        }).orElse(false);
    }
}
