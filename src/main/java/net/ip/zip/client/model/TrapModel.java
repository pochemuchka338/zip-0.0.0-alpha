package net.ip.zip.client.model;

import net.ip.zip.block.entity.TrapBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TrapModel extends GeoModel<TrapBlockEntity> {

    @Override
    public ResourceLocation getModelResource(TrapBlockEntity object) {
        String state = object.getTrapState();
        if ("closed".equals(state)) {
            return new ResourceLocation("zip", "geo/block/trap_closed.geo.json");
        } else {
            return new ResourceLocation("zip", "geo/block/trap_open.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(TrapBlockEntity object) {
        return new ResourceLocation("zip", "textures/block/trap.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TrapBlockEntity object) {
        return new ResourceLocation("zip", "animations/block/trap.animation.json");
    }
}