package net.ip.zip.client.renderer;

import net.ip.zip.client.model.BulletproofVestModel;
import net.ip.zip.item.armor.BulletproofVestItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BulletproofVestRenderer extends GeoArmorRenderer<BulletproofVestItem> {
    public BulletproofVestRenderer() {
        super(new BulletproofVestModel());
    }
}