package net.ip.zip.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TrapSyncS2CPacket {
    private final boolean inTrap;

    public TrapSyncS2CPacket(boolean inTrap) {
        this.inTrap = inTrap;
    }

    public TrapSyncS2CPacket(FriendlyByteBuf buf) {
        this.inTrap = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(inTrap);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getPersistentData().putBoolean("inTrap", this.inTrap);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}