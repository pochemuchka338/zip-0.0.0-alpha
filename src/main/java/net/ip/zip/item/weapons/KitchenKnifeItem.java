package net.ip.zip.item.weapons;

import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.ip.zip.client.model.SimpleGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class KitchenKnifeItem extends GeoWeaponItem {
    public KitchenKnifeItem(net.minecraft.world.item.Tiers tier, int attackDamage, float attackSpeed, net.minecraft.world.item.Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties, "Knife block", "e1f2a3b4-c5d6-7890-abcd-ef1234567894", false);
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(new SimpleGeoModel<>("kitchen_knife"));
    }
}