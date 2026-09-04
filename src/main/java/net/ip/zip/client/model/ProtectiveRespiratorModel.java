package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.armor.ProtectiveRespiratorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ProtectiveRespiratorModel extends GeoModel<ProtectiveRespiratorItem> {
    @Override
    public ResourceLocation getModelResource(ProtectiveRespiratorItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/armor/protective_respirator.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ProtectiveRespiratorItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/armor/protective_respirator.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ProtectiveRespiratorItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/armor/protective_respirator.animation.json");
    }
}