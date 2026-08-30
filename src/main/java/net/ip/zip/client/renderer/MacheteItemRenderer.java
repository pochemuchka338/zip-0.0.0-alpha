package net.ip.zip.client.renderer;

import net.ip.zip.client.model.MacheteItemModel;
import net.ip.zip.item.weapons.MacheteItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MacheteItemRenderer extends GeoItemRenderer<MacheteItem> {
    public MacheteItemRenderer() {
        super(new MacheteItemModel());
    }
}