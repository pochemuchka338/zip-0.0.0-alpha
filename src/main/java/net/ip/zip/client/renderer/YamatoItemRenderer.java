package net.ip.zip.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.item.weapons.YamatoItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class YamatoItemRenderer extends GeoItemRenderer<YamatoItem> {
    public YamatoItemRenderer() {
        super(new SimpleGeoModel<>("yamato"));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        getGeoModel().getBone("sheath").ifPresent(bone -> {
            bone.setHidden(!YamatoItem.isSheathed(stack));
        });
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}