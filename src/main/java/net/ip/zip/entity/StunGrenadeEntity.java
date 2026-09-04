package net.ip.zip.entity;

import net.ip.zip.effect.ModEffects;
import net.ip.zip.item.ModItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class StunGrenadeEntity extends BaseGrenadeEntity {
    private boolean exploding = false;
    private static final double MAX_RADIUS = 15.0D;
    private static final double FADE_START = 10.0D;

    public StunGrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, Level level) {
        super(entityType, level, 80);
    }

    public StunGrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level, 80);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItem.StunGrenade.get();
    }

    @Override
    protected void onDetonate() {
        if (exploding || !this.isAlive()) return;
        exploding = true;
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 2.0f, 1.5f);
            AABB area = new AABB(this.getX() - MAX_RADIUS, this.getY() - MAX_RADIUS, this.getZ() - MAX_RADIUS, this.getX() + MAX_RADIUS, this.getY() + MAX_RADIUS, this.getZ() + MAX_RADIUS);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
            Vec3 grenadePos = this.position();
            for (LivingEntity target : targets) {
                if (!target.isAlive()) continue;
                double dist = target.distanceTo(this);
                if (dist > MAX_RADIUS) continue;
                Vec3 targetEye = new Vec3(target.getX(), target.getEyeY(), target.getZ());
                Vec3 toGrenade = grenadePos.subtract(targetEye);
                Vec3 lookVec = target.getViewVector(1.0f);
                double len = toGrenade.length();
                if (len < 0.001) continue;
                toGrenade = toGrenade.scale(1.0 / len);
                double dot = lookVec.dot(toGrenade);
                double angle = Math.toDegrees(Math.acos(net.minecraft.util.Mth.clamp(dot, -1.0, 1.0)));
                boolean hasLos = this.level().clip(new ClipContext(targetEye, grenadePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target)).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
                if (!hasLos) continue;
                int baseDuration = angle <= 60.0 ? 100 : 10 + this.random.nextInt(11);
                double distFactor = dist <= FADE_START ? 1.0D : Math.max(0.0D, (MAX_RADIUS - dist) / (MAX_RADIUS - FADE_START));
                baseDuration = (int) (baseDuration * distFactor);
                if (baseDuration <= 0) continue;
                if (target instanceof ServerPlayer player) {
                    player.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), baseDuration, 0, false, false, true));
                } else {
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, baseDuration, 0, false, false));
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, baseDuration, 1, false, false));
                }
            }
        } else {
            for (int i = 0; i < 30; i++) {
                double ox = (this.random.nextFloat() - 0.5f) * 2.0;
                double oy = (this.random.nextFloat() - 0.5f) * 2.0;
                double oz = (this.random.nextFloat() - 0.5f) * 2.0;
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.FLASH, this.getX() + ox, this.getY() + oy, this.getZ() + oz, 0, 0, 0);
            }
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
        this.discard();
    }
}