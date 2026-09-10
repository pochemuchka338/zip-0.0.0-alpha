package net.ip.zip.client.renderer;

import net.ip.zip.ZIP;
import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.item.weapons.FlashlightItem;
import net.minecraft.resources.ResourceLocation;

public class FlashlightItemRenderer extends SimpleGeoItemRenderer<FlashlightItem> {
    private static final ResourceLocation TEXTURE_OFF = new ResourceLocation(ZIP.MOD_ID, "textures/item/flashlight.png");
    private static final ResourceLocation TEXTURE_ON = new ResourceLocation(ZIP.MOD_ID, "textures/item/flashlighton.png");

    public FlashlightItemRenderer() {
        super(SimpleGeoModel.weapon("flashlight"));
    }

    @Override
    public ResourceLocation getTextureLocation(FlashlightItem animatable) {
        if (this.currentItemStack != null && this.currentItemStack.hasTag() && this.currentItemStack.getTag().getBoolean("isOn")) {
            return TEXTURE_ON;
        }
        return TEXTURE_OFF;
    }
}