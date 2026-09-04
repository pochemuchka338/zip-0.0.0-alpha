package net.ip.zip.fog;

import net.ip.zip.ZIP;
import net.ip.zip.item.armor.ProtectiveRespiratorItem;
import net.ip.zip.item.armor.RespiratorItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.SuffocationTimerS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID)
public class FogDamageHandler {
    private static final Map<UUID, Float> suffocationTimers = new HashMap<>();
    private static final float MAX_TIMER = 25.0f;
    private static final float RECOVERY_RATE = 1.0f / 20.0f;
    private static final float BUILDUP_RATE = 1.0f / 20.0f;
    private static int syncCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer() == null) return;
        boolean fogActive = ServerFogManager.active && ServerFogManager.transition >= 0.5f;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
            UUID uuid = player.getUUID();
            float timer = suffocationTimers.getOrDefault(uuid, 0.0f);
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            boolean hasMask = head.getItem() instanceof RespiratorItem || head.getItem() instanceof ProtectiveRespiratorItem;

            if (!fogActive || hasMask) {
                if (timer > 0.0f) {
                    timer = Math.max(0.0f, timer - RECOVERY_RATE);
                    suffocationTimers.put(uuid, timer);
                }
            } else {
                timer = Math.min(MAX_TIMER, timer + BUILDUP_RATE);
                suffocationTimers.put(uuid, timer);
                float maxHealth = player.getMaxHealth();
                float damage = timer >= MAX_TIMER ? maxHealth * 0.025f : (maxHealth * 0.04f / 20.0f) * (timer / MAX_TIMER);
                if (damage > 0.0f) player.hurt(player.damageSources().generic(), damage);
            }

            if (syncCounter % 5 == 0) {
                ModMessages.sendToPlayer(new SuffocationTimerS2CPacket(timer), player);
            }
        }
        syncCounter++;
    }
}