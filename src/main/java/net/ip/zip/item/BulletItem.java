package net.ip.zip.item;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BulletItem extends GeoItem {
    public BulletItem(Properties properties) {
        super(properties);
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(SimpleGeoModel.weapon("bullet"));
    }
}