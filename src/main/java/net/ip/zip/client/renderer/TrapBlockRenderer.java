package net.ip.zip.client.renderer;

import net.ip.zip.block.entity.TrapBlockEntity;
import net.ip.zip.client.model.TrapModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TrapBlockRenderer extends GeoBlockRenderer<TrapBlockEntity> {

    public TrapBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new TrapModel());
    }
}