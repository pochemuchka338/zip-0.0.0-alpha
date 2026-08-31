package net.ip.zip.entity;

import net.ip.zip.item.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MolotovEntity extends ThrowableItemProjectile {
    public MolotovEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MolotovEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.Molotov.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 4; i++) {
                double ox = (this.random.nextFloat() - 0.5f) * 0.2;
                double oy = (this.random.nextFloat() - 0.5f) * 0.2;
                double oz = (this.random.nextFloat() - 0.5f) * 0.2;
                this.level().addParticle(ParticleTypes.FLAME, this.getX() + ox, this.getY() + 0.1 + oy, this.getZ() + oz,
                        ox * 0.3, 0.02 + this.random.nextFloat() * 0.03, oz * 0.3);
            }
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.2, this.getZ(),
                    0.0, 0.02, 0.0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            shatter();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        if (!this.isAlive()) return false;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            shatter();
            return true;
        }
        return false;
    }

    private void shatter() {
        if (!this.isAlive()) return;

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.2f, 0.8f + this.random.nextFloat() * 0.4f);

        BlockPos center = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        int fireCount = 18 + this.random.nextInt(19);
        List<BlockPos> firePositions = new ArrayList<>();

        for (int i = 0; i < fireCount; i++) {
            int dx = this.random.nextInt(11) - 5;
            int dz = this.random.nextInt(11) - 5;
            BlockPos target = center.offset(dx, 0, dz);

            for (int dy = 4; dy >= -4; dy--) {
                BlockPos check = target.offset(0, dy, 0);
                BlockState state = this.level().getBlockState(check);
                BlockState above = this.level().getBlockState(check.above());

                if (state.isSolid() && above.isAir()) {
                    firePositions.add(check.above());
                    break;
                }
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            double startX = this.getX();
            double startY = this.getY() + 0.3;
            double startZ = this.getZ();

            for (BlockPos firePos : firePositions) {
                double targetX = firePos.getX() + 0.5;
                double targetY = firePos.getY() + 0.3;
                double targetZ = firePos.getZ() + 0.5;

                double dist = Math.sqrt(
                        (targetX - startX) * (targetX - startX) +
                                (targetY - startY) * (targetY - startY) +
                                (targetZ - startZ) * (targetZ - startZ));

                if (dist < 0.1) dist = 0.1;

                double speed = 0.25 + this.random.nextDouble() * 0.35;
                double velX = (targetX - startX) / dist * speed;
                double velY = (targetY - startY) / dist * speed;
                double velZ = (targetZ - startZ) / dist * speed;

                for (int p = 0; p < 3; p++) {
                    double spread = 0.08;
                    serverLevel.sendParticles(ParticleTypes.FLAME,
                            startX, startY, startZ, 0,
                            velX + (this.random.nextFloat() - 0.5f) * spread,
                            velY + (this.random.nextFloat() - 0.5f) * spread,
                            velZ + (this.random.nextFloat() - 0.5f) * spread,
                            0.15);
                }
            }
        }

        for (BlockPos firePos : firePositions) {
            this.level().setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
        }

        AABB burnArea = new AABB(this.getX() - 5, this.getY() - 2, this.getZ() - 5,
                this.getX() + 5, this.getY() + 3, this.getZ() + 5);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, burnArea);
        for (LivingEntity entity : entities) {
            if (entity.isAlive()) {
                entity.setSecondsOnFire(5 + this.random.nextInt(4));
            }
        }

        this.discard();
    }
}