package net.ip.zip.client;

import net.ip.zip.ZIP;
import net.ip.zip.client.animation.AnimationHandler;
import net.ip.zip.item.weapons.BaseballBatItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.WeaponAttackC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class BatClientEvents {
    private static final Map<UUID, String> currentAnim = new HashMap<>();
    private static final Map<UUID, Integer> hitCounter = new HashMap<>();
    private static final Map<UUID, Integer> attackHeldTicks = new HashMap<>();
    private static final Map<UUID, Boolean> wasAttackDown = new HashMap<>();
    private static final Map<UUID, Integer> lastAttackTick = new HashMap<>();
    private static final Set<String> ONE_SHOT = Set.of(
            "bat_hit", "bat_hit2", "bat_block_damage", "bat_heavy_blow"
    );
    private static final int ATTACK_COOLDOWN = 16;
    private static final int HEAVY_CHARGE_TICKS = 10;
    private static final String DATA_KEY = "bat_animation";

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        AbstractClientPlayer player = mc.player;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BaseballBatItem)) {
            AnimationHandler.stop(player, DATA_KEY);
            clearState(player.getUUID());
            return;
        }

        UUID uuid = player.getUUID();
        boolean attackDown = mc.options.keyAttack.isDown();
        boolean useDown = mc.options.keyUse.isDown();
        boolean wasDown = wasAttackDown.getOrDefault(uuid, false);
        int heldTicks = attackHeldTicks.getOrDefault(uuid, 0);
        String curAnim = currentAnim.getOrDefault(uuid, "");

        if ("bat_heavy_blow".equals(curAnim)) {
            if (AnimationHandler.isPlaying(player, DATA_KEY)) {
                return;
            }
            currentAnim.remove(uuid);
        }

        if (ONE_SHOT.contains(curAnim) && AnimationHandler.isPlaying(player, DATA_KEY)) {
            return;
        }

        if (!attackDown && wasDown) {
            int lastTick = lastAttackTick.getOrDefault(uuid, -ATTACK_COOLDOWN);
            if (player.tickCount - lastTick >= ATTACK_COOLDOWN) {
                boolean heavy = heldTicks >= HEAVY_CHARGE_TICKS;
                int targetId = -1;
                if (mc.hitResult instanceof EntityHitResult entityHit) {
                    targetId = entityHit.getEntity().getId();
                }
                ModMessages.sendToServer(new WeaponAttackC2SPacket(targetId, heavy));
                lastAttackTick.put(uuid, player.tickCount);

                if (heavy) {
                    playOneShot(player, "bat_heavy_blow", true);
                } else {
                    int counter = hitCounter.getOrDefault(uuid, 0);
                    String anim = (counter % 2 == 0) ? "bat_hit" : "bat_hit2";
                    hitCounter.put(uuid, counter + 1);
                    playOneShot(player, anim, true);
                }
            }
            attackHeldTicks.put(uuid, 0);
            wasAttackDown.put(uuid, false);
            return;
        }

        if (attackDown) {
            attackHeldTicks.put(uuid, heldTicks + 1);
        } else {
            attackHeldTicks.put(uuid, 0);
        }
        wasAttackDown.put(uuid, attackDown);

        if (useDown) {
            if (!"bat_block".equals(curAnim)) {
                AnimationHandler.play(player, DATA_KEY, "bat_block", false);
                currentAnim.put(uuid, "bat_block");
            }
            return;
        }

        double dx = player.getDeltaMovement().x;
        double dz = player.getDeltaMovement().z;
        boolean isMoving = dx * dx + dz * dz > 0.001;
        boolean isSprinting = player.isSprinting();

        String moveAnim;
        if (isSprinting) {
            moveAnim = "bat_sprinting";
        } else if (isMoving) {
            moveAnim = "bat_running";
        } else {
            moveAnim = "bat_idle";
        }

        if (!moveAnim.equals(curAnim)) {
            AnimationHandler.play(player, DATA_KEY, moveAnim, false);
            currentAnim.put(uuid, moveAnim);
        }
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.getMainHandItem().getItem() instanceof BaseballBatItem) {
            if (event.isAttack()) {
                event.setSwingHand(false);
                event.setCanceled(true);
            }
        }
    }

    private static void playOneShot(AbstractClientPlayer player, String animName, boolean firstPerson) {
        UUID uuid = player.getUUID();
        AnimationHandler.play(player, DATA_KEY, animName, firstPerson);
        currentAnim.put(uuid, animName);
    }

    private static void clearState(UUID uuid) {
        currentAnim.remove(uuid);
        hitCounter.remove(uuid);
        attackHeldTicks.remove(uuid);
        wasAttackDown.remove(uuid);
        lastAttackTick.remove(uuid);
    }
}