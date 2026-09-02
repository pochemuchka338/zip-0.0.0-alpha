package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.grenades.Grenade;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GrenadeItemModel extends GeoModel<Grenade> {
    @Override
    public ResourceLocation getModelResource(Grenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/throwables/grenade.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Grenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/entity/throwables/grenade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Grenade animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/throwables/grenade.animation.json");
    }
}