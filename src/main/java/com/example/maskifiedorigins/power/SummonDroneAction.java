package com.example.maskifiedorigins.power;

import com.example.maskifiedorigins.entity.custom.DroneEntity;
import com.example.maskifiedorigins.registry.ModEntities;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SummonDroneAction extends EntityAction<NoConfiguration> {

    public SummonDroneAction() {
        super(NoConfiguration.CODEC);
    }

    @Override
    @SuppressWarnings("resource")
    public void execute(NoConfiguration conifguration, Entity entity) {
        if (!(entity instanceof Player player)) return;
        if (entity.level().isClientSide()) return;
        DroneEntity.summon((ServerLevel) entity.level(), player, ModEntities.DRONE.get());
    }
}
