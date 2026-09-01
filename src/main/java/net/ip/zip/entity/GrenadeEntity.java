package net.ip.zip.entity;

import net.ip.zip.item.ModItem;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GrenadeEntity extends ThrowableItemProjectile {
    private int fuse = 100;
    private boolean exploding = false;
    private boolean hasBounced = false;
    private int groundTicks = 0;

    private float rotX;
    private float rotY;
    private float rotZ;
    private float rotSpeedX;
    private float rotSpeedY;
    private float rotSpeedZ;

    private double lastTickX;
    private double lastTickY;
    private double lastTickZ;

    private static final float GRAVITY = 0.045F;
    private static final double AIR_DRAG = 0.985D;
    private static final double FIRST_BOUNCE_HORIZONTAL = 0.5D;
    private static final double FIRST_BOUNCE_VERTICAL = 0.25D;
    private static final double AFTER_BOUNCE_FRICTION = 0.62D;
    private static final double GROUND_FRICTION = 0.76D;
    private static final double STOP_SPEED_SQR = 0.009D;

    public GrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        initRotation();
        this.lastTickX = this.getX();
        this.lastTickY = this.getY();
        this.lastTickZ = this.getZ();
    }

    public GrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
        initRotation();
        this.lastTickX = this.getX();
        this.lastTickY = this.getY();
        this.lastTickZ = this.getZ();
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
    protected float getGravity() {
        return GRAVITY;
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

        super.tick();

        if (!this.isAlive()) return;

        if (!this.level().isClientSide) {
            Vec3 motion = this.getDeltaMovement();

            if (this.onGround()) {
                this.groundTicks++;
                this.setDeltaMovement(motion.x * GROUND_FRICTION, 0, motion.z * GROUND_FRICTION);

                if (motion.horizontalDistanceSqr() <= STOP_SPEED_SQR && this.groundTicks > 10) {
                    this.setDeltaMovement(0, 0, 0);
                }
            } else {
                this.groundTicks = 0;
                if (!this.hasBounced) {
                    this.setDeltaMovement(motion.scale(AIR_DRAG));
                }
            }

            fuse--;
            if (fuse <= 0) {
                explode();
            }
        } else {
            double dx = this.getX() - this.lastTickX;
            double dy = this.getY() - this.lastTickY;
            double dz = this.getZ() - this.lastTickZ;
            double speed = Math.sqrt(dx * dx + dy * dy + dz * dz);

            float damp = (float) Mth.clamp(0.25 + speed * 6.0, 0.25, 0.97);
            this.rotSpeedX *= damp;
            this.rotSpeedY *= damp;
            this.rotSpeedZ *= damp;

            this.rotX += this.rotSpeedX;
            this.rotY += this.rotSpeedY;
            this.rotZ += this.rotSpeedZ;

            this.lastTickX = this.getX();
            this.lastTickY = this.getY();
            this.lastTickZ = this.getZ();

            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.05, this.getZ(), 0.0, 0.02, 0.0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.exploding || !this.isAlive()) return;

        if (result.getType() == HitResult.Type.BLOCK) {
            this.bounceFromBlock((BlockHitResult) result);
        } else if (result.getType() == HitResult.Type.ENTITY) {
            this.bounceFromEntity((EntityHitResult) result);
        }
    }

    private void bounceFromBlock(BlockHitResult result) {
        Vec3 motion = this.getDeltaMovement();
        Direction.Axis axis = result.getDirection().getAxis();

        if (!this.hasBounced) {
            this.hasBounced = true;
            switch (axis) {
                case Y -> {
                    if (result.getDirection() == Direction.UP) {
                        double bounceY = Math.abs(motion.y) * FIRST_BOUNCE_VERTICAL;
                        if (bounceY < 0.10) {
                            this.setDeltaMovement(motion.x * GROUND_FRICTION, 0, motion.z * GROUND_FRICTION);
                        } else {
                            this.setDeltaMovement(motion.x * FIRST_BOUNCE_HORIZONTAL, bounceY, motion.z * FIRST_BOUNCE_HORIZONTAL);
                        }
                    } else {
                        this.setDeltaMovement(motion.x * FIRST_BOUNCE_HORIZONTAL, -Math.abs(motion.y) * 0.2, motion.z * FIRST_BOUNCE_HORIZONTAL);
                    }
                }
                case X -> this.setDeltaMovement(-motion.x * FIRST_BOUNCE_HORIZONTAL, motion.y * FIRST_BOUNCE_VERTICAL, motion.z * FIRST_BOUNCE_HORIZONTAL);
                case Z -> this.setDeltaMovement(motion.x * FIRST_BOUNCE_HORIZONTAL, motion.y * FIRST_BOUNCE_VERTICAL, -motion.z * FIRST_BOUNCE_HORIZONTAL);
            }
        } else {
            switch (axis) {
                case Y -> this.setDeltaMovement(motion.x * GROUND_FRICTION, 0, motion.z * GROUND_FRICTION);
                case X -> this.setDeltaMovement(-motion.x * AFTER_BOUNCE_FRICTION, motion.y * 0.15, motion.z * AFTER_BOUNCE_FRICTION);
                case Z -> this.setDeltaMovement(motion.x * AFTER_BOUNCE_FRICTION, motion.y * 0.15, -motion.z * AFTER_BOUNCE_FRICTION);
            }
        }

        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.STONE_HIT, SoundSource.NEUTRAL, 0.3f, 0.9f + this.random.nextFloat() * 0.2f);
        }
    }

    private void bounceFromEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            Entity entity = result.getEntity();
            if (entity instanceof LivingEntity living) {
                living.hurt(this.damageSources().thrown(this, this.getOwner()), 1.0F);
            }
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(-motion.x * 0.4, Math.max(0.1, motion.y * 0.25), -motion.z * 0.4);
        this.hasBounced = true;
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