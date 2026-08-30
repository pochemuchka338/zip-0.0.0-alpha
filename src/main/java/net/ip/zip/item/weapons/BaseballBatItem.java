package net.ip.zip.item.weapons;

import net.ip.zip.client.renderer.BatItemRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BaseballBatItem extends GeoWeaponItem {
    private final String batName;

    public BaseballBatItem(String batName, net.minecraft.world.item.Tiers tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties, "Bat block", "b1c2d3e4-f5a6-7890-abcd-ef1234567891");
        this.batName = batName;
    }

    public String getBatName() {
        return batName;
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new BatItemRenderer();
    }
}