package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.entity.BulletEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BulletEntityModel extends GeoModel<BulletEntity> {
    @Override
    public ResourceLocation getModelResource(BulletEntity animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/entity/bullet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BulletEntity animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/entity/bullet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BulletEntity animatable) {
        return null;
    }
}