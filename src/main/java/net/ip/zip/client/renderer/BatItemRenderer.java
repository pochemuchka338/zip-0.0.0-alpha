package net.ip.zip.client.renderer;

import net.ip.zip.item.weapons.BaseballBatItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BatItemRenderer extends GeoItemRenderer<BaseballBatItem> {
    public BatItemRenderer() {
        super(new net.ip.zip.client.model.BatItemModel());
    }
}