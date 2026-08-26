package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginColdBloodedEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins","origin"));

    private static final ResourceLocation COLD_BLOODED = new ResourceLocation("maskifiedorigins", "tropical_storm_dragon");

    private static final float COLD_TEMPERATURE_THRESHOLD = 0.2f;
    private static final String COLD_STATE_KEY = "maskifiedorigins_cold_blooded_active";

    @SubscribeEvent
    @SuppressWarnings("resource")
    public static void onPlayerTick(LivingEvent .LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasColdBlooded(player)) return;

        boolean cold = !player.isUnderWater() && isExposedToCold(player);
        boolean wasCold = player.getPersistentData().getBoolean(COLD_STATE_KEY);

        if (cold && !wasCold) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, -1,0,false,false,true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1,0,false,false,true));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,-1,0,false,false,true));
            player.getPersistentData().putBoolean(COLD_STATE_KEY, true);
        } else if (!cold && wasCold) {
            player.removeEffect(MobEffects.WEAKNESS);
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            player.removeEffect(MobEffects.DIG_SLOWDOWN);
            player.getPersistentData().putBoolean(COLD_STATE_KEY, false);
        }
    }

    @SuppressWarnings("resource")
    private static boolean isExposedToCold(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        if (level.getBiome(pos).value().getBaseTemperature() < COLD_TEMPERATURE_THRESHOLD) return true;
        if (player.isInPowderSnow || player.wasInPowderSnow) return true;

        BlockState feet = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());
        return isSnowOrIce(feet) || isSnowOrIce(below);
    }

    private static boolean isSnowOrIce(BlockState state) {
        return state.is(Blocks.SNOW)
                || state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.POWDER_SNOW)
                || state.is(Blocks.ICE)
                || state.is(Blocks.PACKED_ICE)
                || state.is(Blocks.BLUE_ICE)
                || state.is(Blocks.FROSTED_ICE);
    }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean hasColdBlooded(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(COLD_BLOODED);
        }).orElse(false);
    }
}