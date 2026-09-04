package net.ip.zip.item.grenades;

import net.ip.zip.entity.ModEntities;
import net.ip.zip.entity.StunGrenadeEntity;
import net.ip.zip.item.BaseThrowableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;

public class StunGrenade extends BaseThrowableItem {
    public StunGrenade(Properties properties) {
        super(properties, "stun_grenade", 1.5f);
    }

    @Override
    protected ThrowableItemProjectile createEntity(Player player, Level level) {
        return new StunGrenadeEntity(ModEntities.STUN_GRENADE.get(), player, level);
    }
}