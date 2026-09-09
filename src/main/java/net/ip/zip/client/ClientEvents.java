package net.ip.zip.client;

import net.ip.zip.client.animation.AnimationHandler;
import net.ip.zip.client.layer.GeoArmorLayer;
import net.ip.zip.client.renderer.BulletproofVestRenderer;
import net.ip.zip.client.renderer.ProtectiveRespiratorRenderer;
import net.ip.zip.client.renderer.RespiratorRenderer;
import net.ip.zip.item.armor.BulletproofVestItem;
import net.ip.zip.item.armor.ProtectiveRespiratorItem;
import net.ip.zip.item.armor.RespiratorItem;
import net.ip.zip.item.weapons.YamatoItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.ToggleSheathC2SPacket;
import net.ip.zip.network.packet.WeaponAttackC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClientEvents {
    public static final ClientEvents INSTANCE = new ClientEvents();

    private final Map<UUID, String> currentAnim = new HashMap<>();
    private final Map<UUID, Integer> sheathHitCounter = new HashMap<>();
    private final Map<UUID, Integer> attackHeldTicks = new HashMap<>();
    private final Map<UUID, Boolean> wasAttackDown = new HashMap<>();
    private final Map<UUID, Integer> lastAttackTick = new HashMap<>();
    private final Set<String> ONE_SHOT = Set.of(
            "hit", "hit_sheath", "hit_sheath2", "alt_hit_sheath",
            "heavy_blow", "heavy_blow_sheath", "block_damage", "remove_scabbard"
    );
    private final int ATTACK_COOLDOWN = 16;
    private final int HEAVY_CHARGE_TICKS = 10;
    private final String DATA_KEY = "yamato_animation";

    private boolean spaceWasPressed = false;

    private ClientEvents() {}

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        AbstractClientPlayer player = mc.player;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof YamatoItem)) {
            clearState(player.getUUID());
            return;
        }

        UUID uuid = player.getUUID();
        boolean sheathed = YamatoItem.isSheathed(stack);
        boolean attackDown = mc.options.keyAttack.isDown();
        boolean useDown = mc.options.keyUse.isDown();
        boolean wasDown = wasAttackDown.getOrDefault(uuid, false);
        int heldTicks = attackHeldTicks.getOrDefault(uuid, 0);
        String curAnim = currentAnim.getOrDefault(uuid, "");

        if ("heavy_blow".equals(curAnim) || "heavy_blow_sheath".equals(curAnim)) {
            if (AnimationHandler.isPlaying(player, DATA_KEY)) return;
            currentAnim.remove(uuid);
        }

        if (ONE_SHOT.contains(curAnim) && AnimationHandler.isPlaying(player, DATA_KEY)) return;

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
                    String heavyAnim = sheathed ? "heavy_blow_sheath" : "heavy_blow";
                    playOneShot(player, heavyAnim, true);
                } else {
                    if (sheathed) {
                        int counter = sheathHitCounter.getOrDefault(uuid, 0);
                        String anim = (counter % 2 == 0) ? "hit_sheath" : "hit_sheath2";
                        sheathHitCounter.put(uuid, counter + 1);
                        playOneShot(player, anim, true);
                    } else {
                        playOneShot(player, "hit", true);
                    }
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

        if (ModKeyBindings.TOGGLE_SHEATH.isDown() && player.isShiftKeyDown()) {
            playOneShot(player, "remove_scabbard", true);
            ModMessages.sendToServer(new ToggleSheathC2SPacket());
            return;
        }

        if (useDown && sheathed) {
            if (!"block".equals(curAnim)) {
                AnimationHandler.play(player, DATA_KEY, "block", false);
                currentAnim.put(uuid, "block");
            }
            return;
        }

        double dx = player.getDeltaMovement().x;
        double dz = player.getDeltaMovement().z;
        boolean isMoving = dx * dx + dz * dz > 0.001;
        boolean isSprinting = player.isSprinting();

        String moveAnim;
        if (isSprinting) {
            moveAnim = "sprinting";
        } else if (isMoving) {
            moveAnim = "running";
        } else {
            moveAnim = "idle";
        }

        if (!moveAnim.equals(curAnim)) {
            AnimationHandler.play(player, DATA_KEY, moveAnim, false);
            currentAnim.put(uuid, moveAnim);
        }
    }

    @SubscribeEvent
    public void onMovementInput(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Намертво перехватываем и глушим WASD и Прыжки на клиенте
        if (mc.player.getPersistentData().getBoolean("inBeartrap")) {
            var input = event.getInput();
            input.leftImpulse = 0.0F;
            input.forwardImpulse = 0.0F;
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
        }
    }

    @SubscribeEvent
    public void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.getMainHandItem().getItem() instanceof YamatoItem) {
            if (event.isAttack()) {
                event.setSwingHand(false);
                event.setCanceled(true);
            }
        }
    }

    private void playOneShot(AbstractClientPlayer player, String animName, boolean firstPerson) {
        UUID uuid = player.getUUID();
        AnimationHandler.play(player, DATA_KEY, animName, firstPerson);
        currentAnim.put(uuid, animName);
    }

    private void clearState(UUID uuid) {
        currentAnim.remove(uuid);
        sheathHitCounter.remove(uuid);
        attackHeldTicks.remove(uuid);
        wasAttackDown.remove(uuid);
        lastAttackTick.remove(uuid);
    }

    public static class ModBusEvents {
        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
            addPlayerLayer(event, "default");
            addPlayerLayer(event, "slim");
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, String skin) {
            var renderer = event.getSkin(skin);
            if (renderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer livingRenderer = (LivingEntityRenderer) renderer;
                livingRenderer.addLayer(new GeoArmorLayer<>(livingRenderer, new BulletproofVestRenderer(), EquipmentSlot.CHEST, "bulletproof_vest", BulletproofVestItem.class));
                livingRenderer.addLayer(new GeoArmorLayer<>(livingRenderer, new RespiratorRenderer(), EquipmentSlot.HEAD, "respirator", RespiratorItem.class));
                livingRenderer.addLayer(new GeoArmorLayer<>(livingRenderer, new ProtectiveRespiratorRenderer(), EquipmentSlot.HEAD, "protective_respirator", ProtectiveRespiratorItem.class));
            }
        }
    }
}