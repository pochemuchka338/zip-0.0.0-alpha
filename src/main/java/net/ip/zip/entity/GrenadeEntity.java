package net.ip.zip.entity;

import net.ip.zip.item.ModItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class GrenadeEntity extends BaseGrenadeEntity {
    private boolean exploding = false;

    public GrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, Level level) {
        super(entityType, level, 100);
    }

    public GrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level, 100);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.Grenade.get();
    }

    @Override
    protected void onDetonate() {
        if (exploding || !this.isAlive()) return;
        exploding = true;
        for (GrenadeEntity other : this.level().getEntitiesOfClass(GrenadeEntity.class, this.getBoundingBox().inflate(4.0))) {
            if (other != this && other.isAlive()) other.explode();
        }
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0f, Level.ExplosionInteraction.TNT);
        this.discard();
    }

    private void explode() {
        if (!exploding && this.isAlive()) onDetonate();
    }
}