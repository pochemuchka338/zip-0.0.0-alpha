package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.grenades.SmokeGrenade;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SmokeGrenadeItemModel extends GeoModel<SmokeGrenade> {
    @Override
    public ResourceLocation getModelResource(SmokeGrenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/throwables/smoke_grenade.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SmokeGrenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/entity/throwables/smoke_grenade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmokeGrenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/throwables/smoke_grenade.animation.json");
    }
}