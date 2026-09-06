package net.ip.zip.client;

import net.ip.zip.ZIP;
import net.ip.zip.item.weapons.BaseGunItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.GunAimC2SPacket;
import net.ip.zip.network.packet.GunModeC2SPacket;
import net.ip.zip.network.packet.GunReloadC2SPacket;
import net.ip.zip.network.packet.GunShootC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class GunClientEvents {
    private static final Map<UUID, Boolean> wasAttackDown = new HashMap<>();
    private static final Map<UUID, Integer> lastShotTick = new HashMap<>();
    private static final Map<UUID, Float> recoilPitch = new HashMap<>();
    private static final Map<UUID, Float> recoilYaw = new HashMap<>();
    private static final Map<UUID, Boolean> isAiming = new HashMap<>();
    private static final Map<UUID, Double> savedSensitivity = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        AbstractClientPlayer player = mc.player;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BaseGunItem gun)) {
            if (isAiming.getOrDefault(player.getUUID(), false)) {
                exitAim(player);
            }
            return;
        }

        UUID uuid = player.getUUID();
        boolean attackDown = mc.options.keyAttack.isDown();
        boolean useDown = mc.options.keyUse.isDown();
        boolean wasDown = wasAttackDown.getOrDefault(uuid, false);
        int lastShot = lastShotTick.getOrDefault(uuid, -1000);
        boolean canShoot = !gun.isReloading(stack) && gun.getAmmo(stack) > 0;
        boolean isAuto = gun.isFullAuto(stack);

        if (canShoot && attackDown && player.tickCount - lastShot >= gun.getStats().fireRate()) {
            if (isAuto || !wasDown) {
                ModMessages.sendToServer(new GunShootC2SPacket());
                lastShotTick.put(uuid, player.tickCount);
            }
        }
        wasAttackDown.put(uuid, attackDown);

        if (gun.getStats().hasScope()) {
            if (useDown && !isAiming.getOrDefault(uuid, false)) {
                enterAim(player, gun.getStats().scopeZoom());
                ModMessages.sendToServer(new GunAimC2SPacket(true));
            } else if (!useDown && isAiming.getOrDefault(uuid, false)) {
                exitAim(player);
                ModMessages.sendToServer(new GunAimC2SPacket(false));
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof BaseGunItem gun)) return;

        if (event.getKey() == GLFW.GLFW_KEY_R && event.getAction() == GLFW.GLFW_PRESS) {
            if (!gun.isReloading(stack) && gun.getAmmo(stack) < gun.getStats().magazineSize()) {
                ModMessages.sendToServer(new GunReloadC2SPacket());
            }
        }

        if (event.getKey() == GLFW.GLFW_KEY_V && event.getAction() == GLFW.GLFW_PRESS) {
            ModMessages.sendToServer(new GunModeC2SPacket());
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (isAiming.getOrDefault(mc.player.getUUID(), false)) {
            ItemStack stack = mc.player.getMainHandItem();
            if (stack.getItem() instanceof BaseGunItem gun && gun.getStats().hasScope()) {
                event.setFOV(event.getFOV() / gun.getStats().scopeZoom());
            }
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        UUID uuid = mc.player.getUUID();
        float rp = recoilPitch.getOrDefault(uuid, 0.0f);
        float ry = recoilYaw.getOrDefault(uuid, 0.0f);
        if (Math.abs(rp) > 0.01f || Math.abs(ry) > 0.01f) {
            event.setPitch(event.getPitch() - rp);
            event.setYaw(event.getYaw() + ry);
            recoilPitch.put(uuid, rp * 0.82f);
            recoilYaw.put(uuid, ry * 0.82f);
        }
    }

    public static void applyRecoil(UUID uuid, float pitch, float yaw) {
        recoilPitch.put(uuid, recoilPitch.getOrDefault(uuid, 0.0f) + pitch);
        recoilYaw.put(uuid, recoilYaw.getOrDefault(uuid, 0.0f) + yaw);
    }

    private static void enterAim(AbstractClientPlayer player, float zoom) {
        UUID uuid = player.getUUID();
        isAiming.put(uuid, true);
        savedSensitivity.put(uuid, Minecraft.getInstance().options.sensitivity().get());
        Minecraft.getInstance().options.sensitivity().set(savedSensitivity.get(uuid) / zoom);
    }

    private static void exitAim(AbstractClientPlayer player) {
        UUID uuid = player.getUUID();
        isAiming.put(uuid, false);
        Double sens = savedSensitivity.remove(uuid);
        if (sens != null) {
            Minecraft.getInstance().options.sensitivity().set(sens);
        }
    }

    public static boolean isAiming(UUID uuid) {
        return isAiming.getOrDefault(uuid, false);
    }
}