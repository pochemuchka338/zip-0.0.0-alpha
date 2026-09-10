package net.ip.zip.network.packet;

import net.ip.zip.item.weapons.GlockItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReloadGunC2SPacket {
    public ReloadGunC2SPacket() {}

    public static void encode(ReloadGunC2SPacket msg, FriendlyByteBuf buf) {}

    public static ReloadGunC2SPacket decode(FriendlyByteBuf buf) {
        return new ReloadGunC2SPacket();
    }

    public static void handle(ReloadGunC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof GlockItem gun) {
                gun.reloadGun(stack, player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}