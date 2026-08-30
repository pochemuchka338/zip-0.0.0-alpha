package net.ip.zip.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ip.zip.entity.MeteorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MeteorRenderer extends EntityRenderer<MeteorEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public MeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public ResourceLocation getTextureLocation(MeteorEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(MeteorEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(MeteorEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRotX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRotY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRotZ()));
        poseStack.scale(4.0f, 4.0f, 4.0f);
        poseStack.translate(-0.5, -0.5, -0.5);

        BlockState state = Blocks.COBBLESTONE.defaultBlockState();
        this.blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}