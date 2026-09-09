package net.ip.zip.block.entity;

import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.TrapSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class TrapBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Random random = new Random();
    private String trapState = "open";
    private boolean isActive = true;

    public TrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAP.get(), pos, state);
    }

    public String getTrapState() {
        return this.trapState;
    }

    public void catchEntity(Entity victim) {
        if (!isActive || !victim.isAlive()) return;
        CompoundTag victimData = victim.getPersistentData();
        BlockPos pos = this.getBlockPos();
        if (victimData.getBoolean("inTrap") && victimData.getLong("trapPos") == pos.asLong()) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;

        if (victim instanceof Player player) {
            isActive = false;
            trapState = "closed";
            victimData.putBoolean("inTrap", true);
            victimData.putLong("trapPos", pos.asLong());
            victimData.putBoolean("btCentered", false);

            player.absMoveTo(centerX, player.getY(), centerZ);
            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
            player.hurtMarked = true;

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.teleport(centerX, player.getY(), centerZ, serverPlayer.getYRot(), serverPlayer.getXRot());
            }

            player.hurt(level.damageSources().cactus(), 2.0F);
            playTrapSounds();

            if (player instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToPlayer(new TrapSyncS2CPacket(true), serverPlayer);
            }
            triggerAnimationSync();
        } else if (victim instanceof Mob mob) {
            isActive = false;
            trapState = "closed";
            victimData.putBoolean("inTrap", true);
            victimData.putLong("trapPos", pos.asLong());

            mob.teleportTo(centerX, pos.getY(), centerZ);
            mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0);
            mob.hurt(level.damageSources().cactus(), 5.0F);
            playTrapSounds();
            triggerAnimationSync();
        }
    }

    public void playerTapSpace(Player player) {
        double escapeChance = 0.15;
        if (random.nextDouble() < escapeChance) {
            CompoundTag playerData = player.getPersistentData();
            playerData.putBoolean("inTrap", false);
            playerData.remove("trapPos");
            playerData.remove("btCentered");
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140, 1, false, false));
            level.playSound(null, getBlockPos(), SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.2F);
            if (player instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToPlayer(new TrapSyncS2CPacket(false), serverPlayer);
            }
            this.setChanged();
        } else {
            player.hurt(level.damageSources().cactus(), 1.0F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 0.5F, 1.6F);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TrapBlockEntity blockEntity) {
    }

    private void playTrapSounds() {
        level.playSound(null, getBlockPos(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, 1.4F);
    }

    private void triggerAnimationSync() {
        this.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isActive = tag.contains("isActive") ? tag.getBoolean("isActive") : true;
        this.trapState = tag.getString("trapState");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isActive", this.isActive);
        tag.putString("trapState", this.trapState);
    }

    public void forceOpen() {
        this.isActive = true;
        this.trapState = "open";
        this.setChanged();
        this.triggerAnimationSync();
        if (level != null) {
            level.playSound(null, getBlockPos(), SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}