package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.grenades.StunGrenade;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StunGrenadeItemModel extends GeoModel<StunGrenade> {
    @Override
    public ResourceLocation getModelResource(StunGrenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/throwables/stun_grenade.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StunGrenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/entity/throwables/stun_grenade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StunGrenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/throwables/stun_grenade.animation.json");
    }
}