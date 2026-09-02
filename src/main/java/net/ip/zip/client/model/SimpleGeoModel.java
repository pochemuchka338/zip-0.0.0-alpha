package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SimpleGeoModel<T extends software.bernie.geckolib.animatable.GeoItem> extends GeoModel<T> {
    private final String modelPath;
    private final String texturePath;
    private final String animationPath;

    public SimpleGeoModel(String name) {
        this.modelPath = "geo/weapons/" + name + ".geo.json";
        this.texturePath = "textures/item/" + name + ".png";
        this.animationPath = "animations/" + name + ".animation.json";
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(ZIP.MOD_ID, modelPath);
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(ZIP.MOD_ID, texturePath);
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(ZIP.MOD_ID, animationPath);
    }
}