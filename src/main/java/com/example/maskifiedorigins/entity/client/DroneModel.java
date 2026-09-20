package com.example.maskifiedorigins.entity.client;// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.example.maskifiedorigins.entity.animations.DroneModelAnims;
import com.example.maskifiedorigins.entity.custom.DroneEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import com.example.maskifiedorigins.entity.custom.DroneEntity;

public class DroneModel<T extends DroneEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	private final ModelPart mainbody;
	private final ModelPart landinggearwings;
	private final ModelPart LGLwing;
	private final ModelPart LGRwing;
	private final ModelPart tail;
	private final ModelPart wings;
	private final ModelPart Rrotor;
	private final ModelPart Lrotor;
	private final ModelPart head;
	private final ModelPart root;

	public DroneModel(ModelPart root) {
		this.root = root;
		this.mainbody = root.getChild("mainbody");
		this.landinggearwings = root.getChild("landinggearwings");
		this.LGLwing = this.landinggearwings.getChild("LGLwing");
		this.LGRwing = this.landinggearwings.getChild("LGRwing");
		this.tail = root.getChild("tail");
		this.wings = root.getChild("wings");
		this.Rrotor = this.wings.getChild("Rrotor");
		this.Lrotor = this.wings.getChild("Lrotor");
		this.head = root.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition mainbody = partdefinition.addOrReplaceChild("mainbody", CubeListBuilder.create().texOffs(18, 2).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(18, 20).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, -2.0F));

		PartDefinition landinggearwings = partdefinition.addOrReplaceChild("landinggearwings", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, -1.5F));

		PartDefinition LGLwing = landinggearwings.addOrReplaceChild("LGLwing", CubeListBuilder.create(), PartPose.offset(1.8482F, 2.0F, 0.0863F));

		PartDefinition LGLwing_r1 = LGLwing.addOrReplaceChild("LGLwing_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-4.5F, -2.5F, -0.7F, 6.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1518F, 2.0F, 1.4137F, 0.0F, -0.4712F, 0.829F));

		PartDefinition LGRwing = landinggearwings.addOrReplaceChild("LGRwing", CubeListBuilder.create(), PartPose.offset(-1.8482F, -14.0F, 0.0863F));

		PartDefinition LGRwing_r1 = LGRwing.addOrReplaceChild("LGRwing_r1", CubeListBuilder.create().texOffs(18, 0).addBox(-1.5F, -2.5F, -0.7F, 6.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1518F, 18.0F, 1.4137F, 0.0F, 0.4712F, -0.829F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 0.0F));

		PartDefinition Ltail_r1 = tail.addOrReplaceChild("Ltail_r1", CubeListBuilder.create().texOffs(16, 14).addBox(-1.6F, -1.99F, -0.1F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.1204F, -0.1745F, 0.0F));

		PartDefinition Rtail_r1 = tail.addOrReplaceChild("Rtail_r1", CubeListBuilder.create().texOffs(16, 8).addBox(-0.4F, -1.99F, -0.1F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.1204F, 0.1745F, 0.0F));

		PartDefinition tailunder_r1 = tail.addOrReplaceChild("tailunder_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.36F, -1.2F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition wings = partdefinition.addOrReplaceChild("wings", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -1.0F, -0.7F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(-10.0F, -1.0F, -0.7F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(8, 20).addBox(-6.0F, -0.99F, 0.8F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 21.0F, -3.0F));

		PartDefinition Rrotor = wings.addOrReplaceChild("Rrotor", CubeListBuilder.create().texOffs(9, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -0.5F, 1.25F));

		PartDefinition Lrotor = wings.addOrReplaceChild("Lrotor", CubeListBuilder.create().texOffs(9, 3).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -0.5F, 1.25F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, -3.0F));

		PartDefinition face_r1 = head.addOrReplaceChild("face_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-2.4F, -1.99F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, -1.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition antenna_r1 = head.addOrReplaceChild("antenna_r1", CubeListBuilder.create().texOffs(8, 22).addBox(0.0F, -2.98F, -1.1F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.2074F, -1.169F, 0.2248F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.animate(entity.flyingAnimationState, DroneModelAnims.RotorAnim, ageInTicks);
		this.animate(entity.landingGearAnimationState, DroneModelAnims.WingsLandAnim, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return root;
	}
}