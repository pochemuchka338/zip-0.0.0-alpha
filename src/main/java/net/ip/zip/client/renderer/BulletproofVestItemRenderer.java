package net.ip.zip.client.renderer;

import net.ip.zip.item.armor.BulletproofVestItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BulletproofVestItemRenderer extends GeoItemRenderer<BulletproofVestItem> {
    public BulletproofVestItemRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation("zip", "bulletproof_vest_item")));
    }
}