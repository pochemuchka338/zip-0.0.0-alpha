package net.ip.zip.client.renderer;

import net.ip.zip.client.model.BinocularsItemModel;
import net.ip.zip.item.BinocularsItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BinocularsItemRenderer extends GeoItemRenderer<BinocularsItem> {
    public BinocularsItemRenderer() {
        super(new BinocularsItemModel());
    }
}