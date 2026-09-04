package net.ip.zip.client.renderer;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.item.armor.RespiratorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RespiratorRenderer extends GeoArmorRenderer<RespiratorItem> {
    public RespiratorRenderer() {
        super(SimpleGeoModel.armor("respirator"));
    }
}