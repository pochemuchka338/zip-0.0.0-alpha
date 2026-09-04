package net.ip.zip.item.armor;

import net.ip.zip.client.renderer.BulletproofVestItemRenderer;
import net.minecraft.world.item.ArmorMaterial;

public class BulletproofVestItem extends GeoArmorItem {
    public BulletproofVestItem(ArmorMaterial material, Properties properties) {
        super(material, Type.CHESTPLATE, properties, BulletproofVestItemRenderer::new);
    }
}