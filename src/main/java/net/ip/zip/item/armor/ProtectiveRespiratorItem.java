package net.ip.zip.item.armor;

import net.ip.zip.client.renderer.ProtectiveRespiratorItemRenderer;
import net.minecraft.world.item.ArmorMaterial;

public class ProtectiveRespiratorItem extends GeoArmorItem {
    public ProtectiveRespiratorItem(ArmorMaterial material, Properties properties) {
        super(material, Type.HELMET, properties, ProtectiveRespiratorItemRenderer::new);
    }
}