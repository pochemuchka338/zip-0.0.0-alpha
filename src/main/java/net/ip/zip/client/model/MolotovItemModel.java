package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.grenades.Molotov;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MolotovItemModel extends GeoModel<Molotov> {
    @Override
    public ResourceLocation getModelResource(Molotov animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/throwables/molotov.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Molotov animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/entity/throwables/molotov.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Molotov animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/throwables/molotov.animation.json");
    }
}