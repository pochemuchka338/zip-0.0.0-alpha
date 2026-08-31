package net.ip.zip.client.renderer;

import net.ip.zip.ZIP;
import net.ip.zip.item.armor.BulletproofVestItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BulletproofVestRenderer extends GeoArmorRenderer<BulletproofVestItem> {
    public BulletproofVestRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(ZIP.MOD_ID, "armor/bulletproof_vest")));
    }
}