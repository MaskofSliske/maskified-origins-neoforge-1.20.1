package com.example.maskifiedorigins;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID)

public class OriginFreezeDamageEvents {

    private static final float FREEZE_DAMAGE_MULTIPLIER = 2.0f;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!event.getSource().is(DamageTypes.FREEZE)) return;
        if (!OriginColdBloodedEvents.hasColdBlooded(player)) return;

        event.setAmount(event.getAmount() * FREEZE_DAMAGE_MULTIPLIER);
    }
}