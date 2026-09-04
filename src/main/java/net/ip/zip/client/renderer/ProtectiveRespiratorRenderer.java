package net.ip.zip.client.renderer;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.item.armor.ProtectiveRespiratorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ProtectiveRespiratorRenderer extends GeoArmorRenderer<ProtectiveRespiratorItem> {
    public ProtectiveRespiratorRenderer() {
        super(SimpleGeoModel.armor("protective_respirator"));
    }
}