package net.ip.zip.client.model;

import net.ip.zip.ZIP;
import net.ip.zip.item.weapons.KitchenKnifeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KitchenKnifeItemModel extends GeoModel<KitchenKnifeItem> {
    @Override
    public ResourceLocation getModelResource(KitchenKnifeItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "geo/kitchen_knife.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KitchenKnifeItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "textures/item/kitchen_knife.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KitchenKnifeItem animatable) {
        return new ResourceLocation(ZIP.MOD_ID, "animations/kitchen_knife.animation.json");
    }
}