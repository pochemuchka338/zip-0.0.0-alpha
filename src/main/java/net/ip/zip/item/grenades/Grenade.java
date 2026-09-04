package net.ip.zip.item.grenades;

import net.ip.zip.entity.GrenadeEntity;
import net.ip.zip.entity.ModEntities;
import net.ip.zip.item.BaseThrowableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;

public class Grenade extends BaseThrowableItem {
    public Grenade(Properties properties) {
        super(properties, "grenade", 1.5f);
    }

    @Override
    protected ThrowableItemProjectile createEntity(Player player, Level level) {
        return new GrenadeEntity(ModEntities.GRENADE.get(), player, level);
    }
}