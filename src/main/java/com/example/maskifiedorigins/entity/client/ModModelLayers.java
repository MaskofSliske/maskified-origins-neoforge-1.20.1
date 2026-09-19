package com.example.maskifiedorigins.entity.client;

import com.example.maskifiedorigins.MaskifiedOrigins;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation DRONE_LAYER = new ModelLayerLocation(
            new ResourceLocation(MaskifiedOrigins.MODID, "drone_layer"), "main");
}
