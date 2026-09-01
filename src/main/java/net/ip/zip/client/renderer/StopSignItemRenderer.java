package net.ip.zip.client.renderer;

import net.ip.zip.client.model.StopSignItemModel;
import net.ip.zip.item.weapons.StopSignItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class StopSignItemRenderer extends GeoItemRenderer<StopSignItem> {
    public StopSignItemRenderer() {
        super(new StopSignItemModel());
    }
}