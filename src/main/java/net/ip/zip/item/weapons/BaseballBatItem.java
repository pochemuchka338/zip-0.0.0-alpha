package net.ip.zip.item.weapons;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.minecraft.world.item.Tiers;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BaseballBatItem extends GeoWeaponItem {
    private final String batName;

    public BaseballBatItem(String batName, Tiers tier, int attackDamage, float attackSpeed, net.minecraft.world.item.Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties, "Bat block", "b1c2d3e4-f5a6-7890-abcd-ef1234567891", true);
        this.batName = batName;
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(SimpleGeoModel.weapon(batName));
    }
}