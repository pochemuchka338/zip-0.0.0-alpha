package net.ip.zip.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ip.zip.ZIP;
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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GeoArmorLayer<T extends ArmorItem & GeoItem> extends RenderLayer<Player, PlayerModel<Player>> {
    private final GeoArmorRenderer<T> renderer;
    private final EquipmentSlot slot;
    private final String textureName;
    private final Class<T> itemClass;

    @SuppressWarnings("unchecked")
    public GeoArmorLayer(LivingEntityRenderer<?, ?> livingRenderer, GeoArmorRenderer<T> renderer, EquipmentSlot slot, String textureName, Class<T> itemClass) {
        super((RenderLayerParent<Player, PlayerModel<Player>>) (Object) livingRenderer);
        this.renderer = renderer;
        this.slot = slot;
        this.textureName = textureName;
        this.itemClass = itemClass;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Player player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack stack = player.getItemBySlot(slot);
        if (!itemClass.isInstance(stack.getItem())) return;
        this.renderer.prepForRender(player, stack, slot, this.getParentModel());
        ResourceLocation texture = new ResourceLocation(ZIP.MOD_ID, "textures/armor/" + textureName + ".png");
        RenderType renderType = RenderType.entityTranslucentCull(texture);
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        this.renderer.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}