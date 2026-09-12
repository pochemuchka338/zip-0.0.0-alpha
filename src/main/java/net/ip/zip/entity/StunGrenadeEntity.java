package net.ip.zip.entity;

import net.ip.zip.item.ModItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.AffectedPlayerInfo;
import net.ip.zip.network.packet.StunEffectData;
import net.ip.zip.network.packet.StunExplodedMessage;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class StunGrenadeEntity extends BaseGrenadeEntity {
    private boolean exploding = false;
    private static final double MAX_RADIUS = 15.0D;

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
            List<AffectedPlayerInfo> affected = calculateAffectedPlayers();
            ModMessages.sendToAll(new StunExplodedMessage(this.position(), affected));
        } else {
            for (int i = 0; i < 30; i++) {
                double ox = (this.random.nextFloat() - 0.5f) * 2.0;
                double oy = (this.random.nextFloat() - 0.5f) * 2.0;
                double oz = (this.random.nextFloat() - 0.5f) * 2.0;
                this.level().addParticle(ParticleTypes.FLASH, this.getX() + ox, this.getY() + oy, this.getZ() + oz, 0, 0, 0);
            }
            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
        this.discard();
    }

    private List<AffectedPlayerInfo> calculateAffectedPlayers() {
        List<AffectedPlayerInfo> result = new ArrayList<>();
        AABB area = new AABB(this.getX() - MAX_RADIUS, this.getY() - MAX_RADIUS, this.getZ() - MAX_RADIUS, this.getX() + MAX_RADIUS, this.getY() + MAX_RADIUS, this.getZ() + MAX_RADIUS);
        List<ServerPlayer> players = ((ServerLevel) this.level()).getEntitiesOfClass(ServerPlayer.class, area);
        for (ServerPlayer player : players) {
            StunEffectData data = createEffectData(player);
            if (data.effectSustain > 0) {
                result.add(new AffectedPlayerInfo(player.getUUID(), data));
            }
        }
        return result;
    }

    private StunEffectData createEffectData(Player player) {
        Vec3 playerToFlashVec = this.position().subtract(player.getEyePosition());

        Vec3 lookVec = player.getViewVector(1.0f).normalize();
        Vec3 flashVecNormalized = playerToFlashVec.normalize();

        double dotProduct = Math.max(-1.0, Math.min(1.0, lookVec.dot(flashVecNormalized)));
        double angle = Math.toDegrees(Math.acos(dotProduct));

        double distanceFactor = getDistanceFactor(playerToFlashVec.length());
        double blockingFactor = getBlockingFactor(player);

        double maxDuration = 5.0;
        double minDuration = 1.0;
        double totalTimeTier1 = maxDuration * 0.75;
        double totalTimeTier2 = maxDuration * 0.375;

        double fullyBlindedTime;
        if (angle <= 53.0) fullyBlindedTime = maxDuration * 0.5;
        else if (angle <= 72.0) fullyBlindedTime = totalTimeTier1 * 0.5;
        else if (angle <= 101.0) fullyBlindedTime = totalTimeTier2 * 0.5;
        else fullyBlindedTime = minDuration * 0.1;

        fullyBlindedTime *= distanceFactor * blockingFactor;

        double totalDuration;
        if (angle <= 53.0) totalDuration = maxDuration;
        else if (angle <= 72.0) totalDuration = totalTimeTier1;
        else if (angle <= 101.0) totalDuration = totalTimeTier2;
        else totalDuration = minDuration;

        totalDuration *= distanceFactor * blockingFactor;

        return new StunEffectData(this.position(), 20, (int)(fullyBlindedTime * 1000), (int)((totalDuration - fullyBlindedTime) * 1000), 255);
    }

    private double getDistanceFactor(double distance) {
        double range = MAX_RADIUS;
        double clamped = Math.max(0.0, Math.min(1.0, distance / range));
        return Math.max(1.0 - Math.pow(clamped, 2.0), 0.0);
    }

    private double getBlockingFactor(Player player) {
        HitResult result = this.level().clip(new ClipContext(player.getEyePosition(), this.position(), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS ? 1.0 : 0.0;
    }
}
