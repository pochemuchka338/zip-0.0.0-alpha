package net.ip.zip;

import net.ip.zip.entity.BulletEntity;
import net.ip.zip.entity.ModEntities;
import net.ip.zip.item.weapons.BaseGunItem;
import net.ip.zip.network.ModMessages;
import net.ip.zip.network.packet.GunRecoilS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID)
public class GunServerEvents {
    private static final Map<UUID, Integer> reloadEndTick = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> isAiming = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> lastShotTick = new ConcurrentHashMap<>();

    public static void setAiming(UUID uuid, boolean aiming) {
        isAiming.put(uuid, aiming);
    }

    public static boolean isAiming(UUID uuid) {
        return isAiming.getOrDefault(uuid, false);
    }

    public static void startReload(ServerPlayer player, BaseGunItem gun, ItemStack stack) {
        if (gun.isReloading(stack)) return;
        UUID uuid = player.getUUID();
        int endTick = player.server.getTickCount() + gun.getStats().reloadTime();
        reloadEndTick.put(uuid, endTick);
        gun.setReloading(stack, true);
    }

    public static void finishReload(ServerPlayer player, BaseGunItem gun, ItemStack stack) {
        gun.setAmmo(stack, gun.getStats().magazineSize());
        gun.setReloading(stack, false);
    }

    public static void shoot(ServerPlayer player, BaseGunItem gun, ItemStack stack) {
        UUID uuid = player.getUUID();
        int currentTick = player.server.getTickCount();
        if (lastShotTick.getOrDefault(uuid, -1000) + gun.getStats().fireRate() > currentTick) return;
        if (gun.isReloading(stack)) return;
        if (gun.getAmmo(stack) <= 0) return;

        lastShotTick.put(uuid, currentTick);
        gun.setAmmo(stack, gun.getAmmo(stack) - 1);

        BulletEntity bullet = new BulletEntity(ModEntities.BULLET.get(), player.level());
        bullet.setOwner(player);

        Vec3 look = player.getLookAngle();
        double spawnX = player.getX() + look.x * 0.1;
        double spawnY = player.getEyeY() + look.y * 0.1;
        double spawnZ = player.getZ() + look.z * 0.1;
        bullet.setPos(spawnX, spawnY, spawnZ);

        bullet.setBulletDamage(gun.getStats().damage());
        bullet.setKnockback(0.15f);
        bullet.setPierce(gun.getStats().pierce());
        bullet.setTrail(true);

        boolean aiming = isAiming.getOrDefault(uuid, false);
        float spread = aiming ? gun.getStats().spreadAds() : gun.getStats().spreadHip();
        if (player.isSprinting()) spread *= 1.5f;
        if (!player.onGround()) spread *= 1.3f;

        float xRot = player.getXRot();
        float yRot = player.getYRot();

        if (spread > 0.0f) {
            xRot += (player.level().random.nextFloat() - 0.5f) * spread * 45.0f;
            yRot += (player.level().random.nextFloat() - 0.5f) * spread * 45.0f;
        }

        float f = -Mth.sin(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
        float f1 = -Mth.sin(xRot * ((float)Math.PI / 180F));
        float f2 = Mth.cos(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));

        Vec3 motion = new Vec3(f, f1, f2).normalize().scale(gun.getStats().bulletSpeed());
        bullet.setDeltaMovement(motion);

        player.level().addFreshEntity(bullet);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.7f, 1.5f + player.level().random.nextFloat() * 0.3f);

        float recoilPitch = gun.getStats().recoilPitch() * (0.8f + player.level().random.nextFloat() * 0.4f);
        float recoilYaw = gun.getStats().recoilYaw() * (player.level().random.nextFloat() - 0.5f) * 2.0f;
        ModMessages.sendToPlayer(new GunRecoilS2CPacket(recoilPitch, recoilYaw), player);
    }

    public static void toggleMode(ServerPlayer player, BaseGunItem gun, ItemStack stack) {
        if (!gun.getStats().fullAuto()) return;
        gun.setFullAuto(stack, !gun.isFullAuto(stack));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        int currentTick = event.getServer().getTickCount();

        reloadEndTick.entrySet().removeIf(entry -> {
            if (currentTick >= entry.getValue()) {
                ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof BaseGunItem gun) {
                        finishReload(player, gun, stack);
                    }
                }
                return true;
            }
            return false;
        });
    }
}
