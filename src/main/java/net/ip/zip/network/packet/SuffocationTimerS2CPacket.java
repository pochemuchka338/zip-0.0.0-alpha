package net.ip.zip.network.packet;

import net.ip.zip.client.fog.FogClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SuffocationTimerS2CPacket {
    private final float timer;

    public SuffocationTimerS2CPacket(float timer) {
        this.timer = timer;
    }

    public SuffocationTimerS2CPacket(FriendlyByteBuf buf) {
        this.timer = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(timer);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> FogClientEvents.setSuffocationTimer(timer));
        return true;
    }
}