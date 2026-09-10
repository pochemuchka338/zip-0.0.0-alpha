package net.ip.zip.client.renderer;

import net.ip.zip.client.model.BulletEntityModel;
import net.ip.zip.entity.BulletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BulletEntityRenderer extends GeoEntityRenderer<BulletEntity> {
    public BulletEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BulletEntityModel());
    }

    @Override
    public void render(BulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}