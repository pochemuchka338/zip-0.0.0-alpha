package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.client.BinocularsClientEvents;
import net.ip.zip.item.BinocularsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BinocularsItemModel extends GeoModel<BinocularsItem> {
    @Override
    public ResourceLocation getModelResource(BinocularsItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/items/binoculars.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BinocularsItem animatable) {
        int zoom = BinocularsClientEvents.getZoomIndex();
        String suffix;
        switch (zoom) {
            case 0: suffix = "2x"; break;
            case 1: suffix = "4x"; break;
            case 2: suffix = "8x"; break;
            default: suffix = "2x";
        }
        return new ResourceLocation(ZIP.MOD_ID, "textures/item/binoculars_" + suffix + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(BinocularsItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/items/binoculars.animation.json");
    }
}