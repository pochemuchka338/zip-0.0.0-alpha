package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.weapons.BaseballBatItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BatItemModel extends GeoModel<BaseballBatItem> {
    @Override
    public ResourceLocation getModelResource(BaseballBatItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/weapons/" + animatable.getBatName() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BaseballBatItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/item/" + animatable.getBatName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(BaseballBatItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/" + animatable.getBatName() + ".animation.json");
    }
}