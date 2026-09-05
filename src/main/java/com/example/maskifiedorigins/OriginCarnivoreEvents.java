package com.example.maskifiedorigins;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)
public class OriginCarnivoreEvents {

    private static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY, new ResourceLocation("origins", "origin"));

    private static final Set<ResourceLocation> CARNIVORE_ORIGINS = Set.of(
            new ResourceLocation("maskifiedorigins","abyssal_dragon")
    );

    private static final TagKey<Item> MEAT_TAG = ItemTags.create(new ResourceLocation("origins","meat"));
    private static final TagKey<Item> IGNORE_DIET_TAG = ItemTags.create(new ResourceLocation("origins","ignore_diet"));

    private static final Set<Item> MEAT_ITEMS = Set.of(
            Items.BEEF, Items.COOKED_BEEF,
            Items.PORKCHOP, Items.COOKED_PORKCHOP,
            Items.CHICKEN, Items.COOKED_CHICKEN,
            Items.MUTTON, Items.COOKED_MUTTON,
            Items.RABBIT, Items.COOKED_RABBIT,
            Items.COD, Items.COOKED_COD,
            Items.SALMON, Items.COOKED_SALMON,
            Items.TROPICAL_FISH, Items.PUFFERFISH,
            Items.ROTTEN_FLESH, Items.SPIDER_EYE
    );

    @SubscribeEvent
    public static void onStartedEating(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasCarnivore(player)) return;

        ItemStack stack = event.getItem();
        if (!stack.isEdible()) return;
        if (isMeat(stack)) return;

        event.setDuration(0);
        event.setCanceled(true);
    }

    private static boolean isMeat(ItemStack stack) {
        return stack.is(MEAT_TAG) || stack.is(IGNORE_DIET_TAG) || MEAT_ITEMS.contains(stack.getItem());
    }

    private static boolean hasCarnivore(Player player) {
        LazyOptional<IOriginContainer> containerOpt = IOriginContainer.get(player);
        return containerOpt.map(container -> {
            if (!container.hasOrigin(ORIGIN_LAYER)) return false;
            ResourceKey<Origin> origin = container.getOrigin(ORIGIN_LAYER);
            return CARNIVORE_ORIGINS.contains(origin.location());
        }).orElse(false);
    }
}
