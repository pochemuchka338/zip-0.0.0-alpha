package net.ip.zip.client.renderer;

import net.ip.zip.item.armor.ProtectiveRespiratorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ProtectiveRespiratorItemRenderer extends GeoItemRenderer<ProtectiveRespiratorItem> {
    public ProtectiveRespiratorItemRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation("zip", "protective_respirator_item")));
    }
}