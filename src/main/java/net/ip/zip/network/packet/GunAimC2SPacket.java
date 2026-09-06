package net.ip.zip.network.packet;

import net.ip.zip.GunServerEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GunAimC2SPacket {
    private final boolean aiming;

    public GunAimC2SPacket(boolean aiming) {
        this.aiming = aiming;
    }

    public GunAimC2SPacket(FriendlyByteBuf buf) {
        this.aiming = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(aiming);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getSender() != null) {
                GunServerEvents.setAiming(ctx.getSender().getUUID(), aiming);
            }
        });
        return true;
    }
}