package net.ip.zip.item.armor;

import net.ip.zip.client.renderer.RespiratorItemRenderer;
import net.minecraft.world.item.ArmorMaterial;

public class RespiratorItem extends GeoArmorItem {
    public RespiratorItem(ArmorMaterial material, Properties properties) {
        super(material, Type.HELMET, properties, RespiratorItemRenderer::new);
    }
}