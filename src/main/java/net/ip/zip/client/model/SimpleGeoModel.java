package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class SimpleGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public SimpleGeoModel(String path, String name, String texPath, String texName, String animPath, String animName) {
        this.model = new ResourceLocation(ZIP.MOD_ID, path + "/" + name + ".geo.json");
        this.texture = new ResourceLocation(ZIP.MOD_ID, texPath + "/" + texName + ".png");
        this.animation = new ResourceLocation(ZIP.MOD_ID, animPath + "/" + animName + ".animation.json");
    }

    public static <T extends GeoAnimatable> SimpleGeoModel<T> weapon(String name) {
        return new SimpleGeoModel<>("geo/weapons", name, "textures/item", name, "animations", name);
    }

    public static <T extends GeoAnimatable> SimpleGeoModel<T> item(String name) {
        return new SimpleGeoModel<>("geo/items", name, "textures/item", name, "animations/items", name);
    }

    public static <T extends GeoAnimatable> SimpleGeoModel<T> throwable(String name) {
        return new SimpleGeoModel<>("geo/throwables", name, "textures/entity/throwables", name, "animations/throwables", name);
    }

    public static <T extends GeoAnimatable> SimpleGeoModel<T> armor(String name) {
        return new SimpleGeoModel<>("geo/armor", name, "textures/armor", name, "animations/armor", name);
    }

    public static <T extends GeoAnimatable> SimpleGeoModel<T> medical(String name) {
        return new SimpleGeoModel<>("geo", name, "textures/item", name, "animations", name);
    }

    public static <T extends GeoAnimatable> SimpleGeoModel<T> block(String name) {
        return new SimpleGeoModel<>("geo/block", name, "textures/block", name, "animations/block", name);
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animation;
    }
}