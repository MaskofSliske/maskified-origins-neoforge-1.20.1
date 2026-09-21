package com.example.maskifiedorigins.registry;

import com.example.maskifiedorigins.MaskifiedOrigins;
import com.example.maskifiedorigins.entity.custom.DroneEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MaskifiedOrigins.MODID);

    public static final RegistryObject<EntityType<DroneEntity>> DRONE = ENTITY_TYPES.register("avali_drone",
            () -> EntityType.Builder.of(DroneEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build("avali_drone"));
}
