package net.ip.zip.entity;

import net.ip.zip.item.ModItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GrenadeEntity extends ThrowableItemProjectile {
    private int fuse = 100;
    private boolean exploding = false;

    private float rotX;
    private float rotY;
    private float rotZ;
    private float rotSpeedX;
    private float rotSpeedY;
    private float rotSpeedZ;

    public GrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        initRotation();
    }

    public GrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
        initRotation();
    }

    private void initRotation() {
        this.rotX = this.random.nextFloat() * 360;
        this.rotY = this.random.nextFloat() * 360;
        this.rotZ = this.random.nextFloat() * 360;
        this.rotSpeedX = (this.random.nextFloat() - 0.5f) * 24;
        this.rotSpeedY = (this.random.nextFloat() - 0.5f) * 24;
        this.rotSpeedZ = (this.random.nextFloat() - 0.5f) * 24;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.Grenade.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        if (!this.isAlive()) return false;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.explode();
            return true;
        }
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
        if (Math.abs(x) > 1.5 || Math.abs(y) > 1.5 || Math.abs(z) > 1.5) {
            return;
        }
        super.push(x, y, z);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            this.setSharedFlag(6, this.isCurrentlyGlowing());
        }
        this.baseTick();
        if (!this.isAlive()) return;

        boolean wasOnGround = this.onGround();

        Vec3 motionBefore = this.getDeltaMovement();
        if (!wasOnGround) {
            motionBefore = motionBefore.add(0, -0.04, 0);
            this.setDeltaMovement(motionBefore);
        }

        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();

        this.move(MoverType.SELF, motionBefore);

        double expectedX = oldX + motionBefore.x;
        double expectedY = oldY + motionBefore.y;
        double expectedZ = oldZ + motionBefore.z;

        boolean hitX = Math.abs(this.getX() - expectedX) > 0.001 && Math.abs(motionBefore.x) > 0.001;
        boolean hitZ = Math.abs(this.getZ() - expectedZ) > 0.001 && Math.abs(motionBefore.z) > 0.001;
        boolean hitYDown = (this.getY() - expectedY) > 0.001 && motionBefore.y < -0.05;
        boolean hitYUp = (expectedY - this.getY()) > 0.001 && motionBefore.y > 0.001;

        boolean bounced = false;

        if (hitX || hitZ) {
            this.setDeltaMovement(-motionBefore.x * 0.2, this.getDeltaMovement().y, -motionBefore.z * 0.2);
            bounced = true;
        }

        if (hitYDown && !wasOnGround) {
            double bounceY = -motionBefore.y * 0.5;
            if (bounceY < 0.06) {
                this.setDeltaMovement(this.getDeltaMovement().x * 0.2, 0, this.getDeltaMovement().z * 0.2);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().x * 0.2, bounceY, this.getDeltaMovement().z * 0.2);
            }
            bounced = true;
        }

        if (hitYUp) {
            this.setDeltaMovement(this.getDeltaMovement().x, -motionBefore.y * 0.3, this.getDeltaMovement().z);
            bounced = true;
        }

        if (this.onGround() && !bounced) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.9, 0, motion.z * 0.9);
        }

        if (bounced && !this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.STONE_HIT, SoundSource.NEUTRAL, 0.3f, 0.9f + this.random.nextFloat() * 0.2f);
        }

        if (!this.level().isClientSide) {
            fuse--;
            if (fuse <= 0) {
                explode();
            }
        } else {
            if (!this.onGround() || motionBefore.y > 0) {
                this.rotX += this.rotSpeedX;
                this.rotY += this.rotSpeedY;
                this.rotZ += this.rotSpeedZ;
            } else {
                this.rotSpeedX *= 0.75f;
                this.rotSpeedY *= 0.75f;
                this.rotSpeedZ *= 0.75f;
                if (Math.abs(this.rotSpeedX) > 0.1f || Math.abs(this.rotSpeedY) > 0.1f || Math.abs(this.rotSpeedZ) > 0.1f) {
                    this.rotX += this.rotSpeedX;
                    this.rotY += this.rotSpeedY;
                    this.rotZ += this.rotSpeedZ;
                }
            }
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.05, this.getZ(), 0.0, 0.02, 0.0);
        }
    }

    private void explode() {
        if (exploding || !this.isAlive()) return;
        exploding = true;

        for (GrenadeEntity other : this.level().getEntitiesOfClass(GrenadeEntity.class, this.getBoundingBox().inflate(4.0))) {
            if (other != this && other.isAlive()) {
                other.explode();
            }
        }

        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0f, Level.ExplosionInteraction.TNT);
        this.discard();
    }

    public float getRotX() {
        return this.rotX;
    }

    public float getRotY() {
        return this.rotY;
    }

    public float getRotZ() {
        return this.rotZ;
    }
}