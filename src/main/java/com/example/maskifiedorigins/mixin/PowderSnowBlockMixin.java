package com.example.maskifiedorigins.mixin;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins", "origin"));
    private static final ResourceLocation AVALI = new ResourceLocation("maskifiedorigins", "avali");

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void maskifiedorigins$canEntityWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof Player player)) return;

        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        boolean isAvali = containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return origin.location().equals(AVALI);
        }).orElse(false);

        if (isAvali) cir.setReturnValue(true);
    }
}