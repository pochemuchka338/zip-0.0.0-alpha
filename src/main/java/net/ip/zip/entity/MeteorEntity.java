package net.ip.zip.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.MeteorImpactS2CPacket;

public class MeteorEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> DATA_TARGET = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_HAS_TARGET = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.BOOLEAN);

    private Vec3 startPos;
    private int flightTime;
    private static final int MAX_FLIGHT = 600;

    private float rotX;
    private float rotY;
    private float rotZ;
    private float rotSpeedX;
    private float rotSpeedY;
    private float rotSpeedZ;

    public MeteorEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.rotX = this.random.nextFloat() * 360;
        this.rotY = this.random.nextFloat() * 360;
        this.rotZ = this.random.nextFloat() * 360;
        this.rotSpeedX = (this.random.nextFloat() - 0.5f) * 14;
        this.rotSpeedY = (this.random.nextFloat() - 0.5f) * 14;
        this.rotSpeedZ = (this.random.nextFloat() - 0.5f) * 14;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TARGET, BlockPos.ZERO);
        this.entityData.define(DATA_HAS_TARGET, false);
    }

    public void setTarget(BlockPos target) {
        this.entityData.set(DATA_TARGET, target);
        this.entityData.set(DATA_HAS_TARGET, true);

        double azimuth = this.random.nextDouble() * Math.PI * 2;
        double angleFromHorizontal = Math.toRadians(30 + this.random.nextDouble() * 50);
        double height = 600.0;
        double horizontalDist = height / Math.tan(angleFromHorizontal);

        double offsetX = Math.cos(azimuth) * horizontalDist;
        double offsetZ = Math.sin(azimuth) * horizontalDist;

        this.startPos = new Vec3(
                target.getX() + 0.5 + offsetX,
                target.getY() + 0.5 + height,
                target.getZ() + 0.5 + offsetZ
        );
        this.setPos(this.startPos);
        this.setNoGravity(true);
        this.flightTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.rotX += this.rotSpeedX;
            this.rotY += this.rotSpeedY;
            this.rotZ += this.rotSpeedZ;
            return;
        }

        if (!this.entityData.get(DATA_HAS_TARGET)) return;
        if (this.flightTime >= MAX_FLIGHT) {
            if (this.level() instanceof ServerLevel serverLevel) {
                ModMessages.sendToPlayersNear(
                        new MeteorImpactS2CPacket(this.getX(), this.getY(), this.getZ(), 20.0f),
                        serverLevel, this.getX(), this.getY(), this.getZ(), 256.0
                );
            }
            this.level().explode(null, this.getX(), this.getY(), this.getZ(), 4.0f, Level.ExplosionInteraction.MOB);
            this.discard();
            return;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 8, 0.8, 0.8, 0.8, 0.05);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 5, 0.8, 0.8, 0.8, 0.02);
        }

        BlockPos target = this.entityData.get(DATA_TARGET);
        Vec3 end = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        float progress = (float) this.flightTime / (float) MAX_FLIGHT;
        Vec3 current = new Vec3(
                this.startPos.x + (end.x - this.startPos.x) * progress,
                this.startPos.y + (end.y - this.startPos.y) * progress,
                this.startPos.z + (end.z - this.startPos.z) * progress
        );
        this.setPos(current);
        this.flightTime++;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Target")) {
            this.entityData.set(DATA_TARGET, BlockPos.of(tag.getLong("Target")));
            this.entityData.set(DATA_HAS_TARGET, true);
        }
        if (tag.contains("StartX")) {
            this.startPos = new Vec3(tag.getDouble("StartX"), tag.getDouble("StartY"), tag.getDouble("StartZ"));
        }
        this.flightTime = tag.getInt("FlightTime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("Target", this.entityData.get(DATA_TARGET).asLong());
        tag.putBoolean("HasTarget", this.entityData.get(DATA_HAS_TARGET));
        if (this.startPos != null) {
            tag.putDouble("StartX", this.startPos.x);
            tag.putDouble("StartY", this.startPos.y);
            tag.putDouble("StartZ", this.startPos.z);
        }
        tag.putInt("FlightTime", this.flightTime);
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