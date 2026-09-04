package net.ip.zip.client.renderer;

import net.ip.zip.block.entity.StopSignBlockEntity;
import net.ip.zip.client.model.SimpleGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StopSignBlockRenderer extends GeoBlockRenderer<StopSignBlockEntity> {
    @SuppressWarnings("unchecked")
    public StopSignBlockRenderer() {
        super((SimpleGeoModel<StopSignBlockEntity>) (Object) SimpleGeoModel.block("stop_sign"));
    }
}