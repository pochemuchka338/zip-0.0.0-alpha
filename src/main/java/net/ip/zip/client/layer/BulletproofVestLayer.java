package net.ip.zip.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ip.zip.ZIP;
import net.ip.zip.client.renderer.BulletproofVestRenderer;
import net.ip.zip.item.armor.BulletproofVestItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BulletproofVestLayer extends RenderLayer<Player, PlayerModel<Player>> {
    private final BulletproofVestRenderer renderer = new BulletproofVestRenderer();

    @SuppressWarnings("unchecked")
    public BulletproofVestLayer(LivingEntityRenderer<?, ?> livingRenderer) {
        super((RenderLayerParent<Player, PlayerModel<Player>>) (Object) livingRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Player player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof BulletproofVestItem)) return;

        this.renderer.prepForRender(player, chest, EquipmentSlot.CHEST, this.getParentModel());

        ResourceLocation texture = new ResourceLocation(ZIP.MOD_ID, "textures/armor/bulletproof_vest.png");
        RenderType renderType = RenderType.entityTranslucentCull(texture);
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

        this.renderer.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}