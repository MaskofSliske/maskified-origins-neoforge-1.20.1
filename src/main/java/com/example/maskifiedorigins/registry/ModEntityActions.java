package com.example.maskifiedorigins.registry;

import com.example.maskifiedorigins.MaskifiedOrigins;
import com.example.maskifiedorigins.power.SummonDroneAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityActions {
    public static final DeferredRegister<EntityAction<?>> ENTITY_ACTIONS =
            DeferredRegister.create(ApoliRegistries.ENTITY_ACTION_KEY.location(), MaskifiedOrigins.MODID);

    public static final RegistryObject<SummonDroneAction> SUMMON_DRONE = ENTITY_ACTIONS.register("summon_drone", SummonDroneAction::new);
}
