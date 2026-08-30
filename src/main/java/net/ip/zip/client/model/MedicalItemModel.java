package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.MedicalItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MedicalItemModel extends GeoModel<MedicalItem> {
    @Override
    public ResourceLocation getModelResource(MedicalItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/" + animatable.getModelName() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MedicalItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/item/" + animatable.getModelName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(MedicalItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/" + animatable.getModelName() + ".animation.json");
    }
}