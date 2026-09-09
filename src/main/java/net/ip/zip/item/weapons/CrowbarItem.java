package net.ip.zip.item.weapons;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.minecraft.world.item.Tiers;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CrowbarItem extends GeoWeaponItem {
    public CrowbarItem(Properties properties) {
        super(Tiers.IRON, 3, -2.4f, properties, "crowbar_block_slow", "8c12a7d4-f6d3-4a1e-bc43-2287f3b890a1", true);
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(SimpleGeoModel.weapon("crowbar"));
    }
}