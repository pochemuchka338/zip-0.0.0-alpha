package net.ip.zip.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BulletEntity extends Projectile {
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(BulletEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_KNOCKBACK = SynchedEntityData.defineId(BulletEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_TRAIL = SynchedEntityData.defineId(BulletEntity.class, EntityDataSerializers.BOOLEAN);

    private int maxLife = 100;
    private int pierce = 0;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public void setBulletDamage(float damage) {
        this.entityData.set(DATA_DAMAGE, damage);
    }

    public void setKnockback(float knockback) {
        this.entityData.set(DATA_KNOCKBACK, knockback);
    }

    public void setTrail(boolean trail) {
        this.entityData.set(DATA_TRAIL, trail);
    }

    public void setPierce(int pierce) {
        this.pierce = pierce;
    }

    public float getBulletDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public float getKnockback() {
        return this.entityData.get(DATA_KNOCKBACK);
    }

    public boolean hasTrail() {
        return this.entityData.get(DATA_TRAIL);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DAMAGE, 5.0f);
        this.entityData.define(DATA_KNOCKBACK, 0.15f);
        this.entityData.define(DATA_TRAIL, true);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();
        Vec3 start = this.position();
        Vec3 end = start.add(motion);

        if (!this.level().isClientSide) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, e -> !e.isSpectator() && e.isPickable() && e != this.getOwner());
            if (hit.getType() != HitResult.Type.MISS) {
                end = hit.getLocation();
            }

            BlockHitResult blockHit = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (blockHit.getType() != HitResult.Type.MISS) {
                onHitBlock(blockHit);
                if (pierce <= 0) {
                    this.discard();
                    return;
                }
                pierce--;
            }

            if (hit.getType() == HitResult.Type.ENTITY) {
                onHitEntity((EntityHitResult) hit);
                if (pierce <= 0) {
                    this.discard();
                    return;
                }
                pierce--;
            }
        }

        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);

        if (this.tickCount > maxLife) {
            this.discard();
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        Vec3 pos = result.getLocation();
        if (!this.level().isClientSide) {
            this.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.STONE_HIT, SoundSource.NEUTRAL, 0.4f, 1.4f);
        }
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living) {
            LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null;
            living.hurt(this.damageSources().mobProjectile(this, owner), getBulletDamage());
            living.knockback(getKnockback(), -this.getDeltaMovement().x, -this.getDeltaMovement().z);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Damage", getBulletDamage());
        tag.putFloat("Knockback", getKnockback());
        tag.putInt("Pierce", pierce);
        tag.putBoolean("Trail", hasTrail());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Damage")) setBulletDamage(tag.getFloat("Damage"));
        if (tag.contains("Knockback")) setKnockback(tag.getFloat("Knockback"));
        if (tag.contains("Pierce")) pierce = tag.getInt("Pierce");
        if (tag.contains("Trail")) setTrail(tag.getBoolean("Trail"));
    }
}
