package net.ip.zip.item.weapons;

import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.ip.zip.client.model.SimpleGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MacheteItem extends GeoWeaponItem {
    public MacheteItem(net.minecraft.world.item.Tiers tier, int attackDamage, float attackSpeed, net.minecraft.world.item.Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties, "Machete block", "d1e2f3a4-b5c6-7890-abcd-ef1234567893", false);
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(new SimpleGeoModel<>("machete"));
    }
}