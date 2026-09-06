package net.ip.zip.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.ip.zip.ZIP;
import net.ip.zip.entity.BulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BulletRenderer extends EntityRenderer<BulletEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ZIP.MOD_ID, "textures/entity/bullet.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Vec3 motion = entity.getDeltaMovement();
        float yaw = (float) (Mth.atan2(motion.x, motion.z) * (180.0 / Math.PI));
        float pitch = (float) (Mth.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z)) * (180.0 / Math.PI));

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.scale(0.08f, 0.08f, 0.5f);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        VertexConsumer vc = buffer.getBuffer(RENDER_TYPE);

        vertex(matrix, normal, vc, -1, -1, 0, 0, 1, packedLight);
        vertex(matrix, normal, vc, 1, -1, 0, 1, 1, packedLight);
        vertex(matrix, normal, vc, 1, 1, 0, 1, 0, packedLight);
        vertex(matrix, normal, vc, -1, 1, 0, 0, 0, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void vertex(Matrix4f matrix, Matrix3f normal, VertexConsumer consumer, int x, int y, int z, int u, int v, int packedLight) {
        consumer.vertex(matrix, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }
}