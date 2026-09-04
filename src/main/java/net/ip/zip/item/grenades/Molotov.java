package net.ip.zip.item.grenades;

import net.ip.zip.entity.ModEntities;
import net.ip.zip.entity.MolotovEntity;
import net.ip.zip.item.BaseThrowableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;

public class Molotov extends BaseThrowableItem {
    public Molotov(Properties properties) {
        super(properties, "molotov", 1.2f);
    }

    @Override
    protected ThrowableItemProjectile createEntity(Player player, Level level) {
        return new MolotovEntity(ModEntities.MOLOTOV.get(), player, level);
    }
}