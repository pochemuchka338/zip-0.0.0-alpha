package net.ip.zip.network.packet;

import net.ip.zip.GunServerEvents;
import net.ip.zip.item.weapons.BaseGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GunModeC2SPacket {
    public GunModeC2SPacket() {}
    public GunModeC2SPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof BaseGunItem gun) {
                GunServerEvents.toggleMode(player, gun, stack);
            }
        });
        return true;
    }
}