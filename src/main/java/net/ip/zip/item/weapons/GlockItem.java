package net.ip.zip.item.weapons;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.ip.zip.item.WeaponItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GlockItem extends BaseGunItem {
    private static final GunStats GLOCK_STATS = new GunStats(
            5.5f, 3.2f, 10, 0.0f, 0.0f, 17, 30, false, 1.0f, 0.8f, 0.2f, 0, false
    );

    public GlockItem(Properties properties) {
        super(GLOCK_STATS, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!stack.getOrCreateTag().contains("Ammo")) {
            setAmmo(stack, 0);
        }
    }

    public void reloadGun(ItemStack stack, Player player) {
        if (player.level().isClientSide) return;

        int currentAmmo = getAmmo(stack);
        int maxAmmo = getStats().magazineSize();
        if (currentAmmo >= maxAmmo) return;

        int needed = maxAmmo - currentAmmo;
        int consumed = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (consumed >= needed) break;
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(WeaponItem.Bullet.get())) {
                int take = Math.min(invStack.getCount(), needed - consumed);
                invStack.shrink(take);
                consumed += take;
            }
        }

        if (consumed > 0) {
            setAmmo(stack, currentAmmo + consumed);
            player.getInventory().setChanged();
        }
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(SimpleGeoModel.weapon("glock"));
    }
}