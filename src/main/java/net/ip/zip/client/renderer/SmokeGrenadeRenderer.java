package net.ip.zip.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ip.zip.entity.SmokeGrenadeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class SmokeGrenadeRenderer extends EntityRenderer<SmokeGrenadeEntity> {
    public SmokeGrenadeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SmokeGrenadeEntity entity) {
        return null;
    }

    @Override
    public void render(SmokeGrenadeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.15, 0.0);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRotX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRotY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRotZ()));
        poseStack.scale(1.2f, 1.2f, 1.2f);
        Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}