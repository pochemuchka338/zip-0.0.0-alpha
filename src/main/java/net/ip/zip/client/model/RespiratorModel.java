package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.armor.RespiratorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RespiratorModel extends GeoModel<RespiratorItem> {
    @Override
    public ResourceLocation getModelResource(RespiratorItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/armor/respirator.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RespiratorItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/armor/respirator.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RespiratorItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/armor/respirator.animation.json");
    }
}