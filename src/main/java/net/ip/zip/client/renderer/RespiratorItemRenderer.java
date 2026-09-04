package net.ip.zip.client.renderer;

import net.ip.zip.item.armor.RespiratorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RespiratorItemRenderer extends GeoItemRenderer<RespiratorItem> {
    public RespiratorItemRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation("zip", "respirator_item")));
    }
}