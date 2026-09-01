package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.armor.BulletproofVestItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BulletproofVestModel extends GeoModel<BulletproofVestItem> {
    @Override
    public ResourceLocation getModelResource(BulletproofVestItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/armor/bulletproof_vest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BulletproofVestItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/armor/bulletproof_vest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BulletproofVestItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/armor/bulletproof_vest.animation.json");
    }
}