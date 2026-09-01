package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.weapons.StopSignItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StopSignItemModel extends GeoModel<StopSignItem> {
    @Override
    public ResourceLocation getModelResource(StopSignItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/stop_sign.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StopSignItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/item/stop_sign.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StopSignItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/stop_sign.animation.json");
    }
}