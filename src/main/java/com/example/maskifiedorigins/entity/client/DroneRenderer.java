package com.example.maskifiedorigins.entity.client;

import com.example.maskifiedorigins.MaskifiedOrigins;
import com.example.maskifiedorigins.entity.custom.DroneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DroneRenderer extends MobRenderer<DroneEntity, DroneModel<DroneEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MaskifiedOrigins.MODID, "textures/entity/dronetexture.png");

    public DroneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DroneModel<>(ctx.bakeLayer(ModModelLayers.DRONE_LAYER)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity entity) {
        return TEXTURE;
    }
}