package net.ip.zip.client.renderer;

import net.ip.zip.client.model.KitchenKnifeItemModel;
import net.ip.zip.item.weapons.KitchenKnifeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class KitchenKnifeItemRenderer extends GeoItemRenderer<KitchenKnifeItem> {
    public KitchenKnifeItemRenderer() {
        super(new KitchenKnifeItemModel());
    }
}