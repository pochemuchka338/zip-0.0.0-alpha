package net.ip.zip.client.renderer;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SimpleGeoItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    public SimpleGeoItemRenderer(GeoModel<T> model) {
        super(model);
    }
}