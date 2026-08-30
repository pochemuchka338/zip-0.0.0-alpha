package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.weapons.MacheteItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MacheteItemModel extends GeoModel<MacheteItem> {
    @Override
    public ResourceLocation getModelResource(MacheteItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/machete.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MacheteItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/item/machete.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MacheteItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/machete.animation.json");
    }
}