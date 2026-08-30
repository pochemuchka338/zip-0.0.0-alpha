package net.ip.zip;

import net.ip.zip.item.weapons.BaseballBatItem;
import net.ip.zip.item.weapons.YamatoItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.PlayAnimS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID)
public class ServerEvents {
    private static final Map<UUID, Integer> heavyEndTick = new HashMap<>();
    private static final int HEAVY_LOCK_TICKS = 23;

    public static void setHeavyLock(ServerPlayer player) {
        heavyEndTick.put(player.getUUID(), player.server.getTickCount() + HEAVY_LOCK_TICKS);
    }

    public static boolean isHeavyLocked(ServerPlayer player) {
        Integer end = heavyEndTick.get(player.getUUID());
        if (end == null) return false;
        if (player.server.getTickCount() >= end) {
            heavyEndTick.remove(player.getUUID());
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!isHeavyLocked(player)) return;
        player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
        player.hurtMarked = true;
        if (player.isUsingItem()) {
            player.stopUsingItem();
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isHeavyLocked(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (isHeavyLocked(player)) {
            event.setCanceled(true);
            return;
        }

        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof YamatoItem) {
            if (!YamatoItem.isSheathed(stack)) return;
            if (!player.isUsingItem()) return;
            event.setCanceled(true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.0f);
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            ModMessages.sendToPlayer(new PlayAnimS2CPacket("yamato_animation", "block_damage", true), player);
            return;
        }

        if (stack.getItem() instanceof BaseballBatItem) {
            if (!player.isUsingItem()) return;
            event.setCanceled(true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.0f);
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            ModMessages.sendToPlayer(new PlayAnimS2CPacket("bat_animation", "bat_block_damage", false), player);
        }
    }
}