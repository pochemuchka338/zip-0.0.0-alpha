package net.ip.zip.item.weapons;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.ip.zip.item.GeoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FlashlightItem extends GeoItem {
    public FlashlightItem(Properties properties) {
        super(properties);
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(SimpleGeoModel.weapon("flashlight"));
    }
}