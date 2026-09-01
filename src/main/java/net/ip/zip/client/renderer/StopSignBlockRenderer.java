package net.ip.zip.client.renderer;

import net.ip.zip.ZIP;
import net.ip.zip.block.entity.StopSignBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StopSignBlockRenderer extends GeoBlockRenderer<StopSignBlockEntity> {
    public StopSignBlockRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(StopSignBlockEntity animatable) {
                return new ResourceLocation(ZIP.MOD_ID, "geo/block/stop_sign.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(StopSignBlockEntity animatable) {
                return new ResourceLocation(ZIP.MOD_ID, "textures/block/stop_sign.png");
            }

            @Override
            public ResourceLocation getAnimationResource(StopSignBlockEntity animatable) {
                return new ResourceLocation(ZIP.MOD_ID, "animations/block/stop_sign.animation.json");
            }
        });
    }
}