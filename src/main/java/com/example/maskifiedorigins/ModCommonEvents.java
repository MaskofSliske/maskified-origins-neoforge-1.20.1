package com.example.maskifiedorigins;

import com.example.maskifiedorigins.entity.custom.DroneEntity;
import com.example.maskifiedorigins.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaskifiedOrigins.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {
    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent e) {
        e.put(ModEntities.DRONE.get(), DroneEntity.createAttributes().build());
    }
}