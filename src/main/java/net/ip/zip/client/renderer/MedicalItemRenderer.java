package net.ip.zip.client.renderer;

import net.ip.zip.client.model.MedicalItemModel;
import net.ip.zip.item.MedicalItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MedicalItemRenderer extends GeoItemRenderer<MedicalItem> {
    public MedicalItemRenderer() {
        super(new MedicalItemModel());
    }
}