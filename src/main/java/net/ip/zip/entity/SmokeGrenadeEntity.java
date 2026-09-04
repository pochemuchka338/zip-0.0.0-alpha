package net.ip.zip.entity;

import net.ip.zip.item.ModItem;
import net.ip.zip.particle.ModParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SmokeGrenadeEntity extends BaseGrenadeEntity {
    private boolean smoking = false;
    private int smokeTime = 300;

    public SmokeGrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, Level level) {
        super(entityType, level, 120);
    }

    public SmokeGrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level, 120);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.SmokeGrenade.get();
    }

    @Override
    public void tick() {
        if (smoking) {
            smokeTime--;
            if (smokeTime <= 0) {
                this.discard();
                return;
            }
            if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                for (int i = 0; i < 20; i++) {
                    double r = Math.cbrt(this.random.nextDouble());
                    double theta = Math.acos(2 * this.random.nextDouble() - 1);
                    double phi = this.random.nextDouble() * 2 * Math.PI;
                    double ox = r * Math.sin(theta) * Math.cos(phi) * 7.5;
                    double oy = r * Math.cos(theta) * 4.5 + 0.3;
                    double oz = r * Math.sin(theta) * Math.sin(phi) * 7.5;
                    serverLevel.sendParticles(ModParticles.SMOKE_GRENADE_SMOKE.get(), this.getX() + ox, this.getY() + oy, this.getZ() + oz, 0, 0, 0.02, 0, 0.05);
                }
            }
            return;
        }
        super.tick();
    }

    @Override
    protected void onHit(net.minecraft.world.phys.HitResult result) {
        if (smoking || !this.isAlive()) return;
        super.onHit(result);
    }

    @Override
    protected void onDetonate() {
        if (smoking) return;
        smoking = true;
        this.setDeltaMovement(0, 0, 0);
        this.setNoGravity(true);
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH, net.minecraft.sounds.SoundSource.NEUTRAL, 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        }
    }
}