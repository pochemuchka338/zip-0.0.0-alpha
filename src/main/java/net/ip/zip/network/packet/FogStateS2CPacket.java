package net.ip.zip.network.packet;

import net.ip.zip.client.fog.FogManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FogStateS2CPacket {
    private final float fogStart;
    private final float fogEnd;
    private final float fogRed;
    private final float fogGreen;
    private final float fogBlue;
    private final float transition;

    public FogStateS2CPacket(float fogStart, float fogEnd, float fogRed, float fogGreen, float fogBlue, float transition) {
        this.fogStart = fogStart;
        this.fogEnd = fogEnd;
        this.fogRed = fogRed;
        this.fogGreen = fogGreen;
        this.fogBlue = fogBlue;
        this.transition = transition;
    }

    public FogStateS2CPacket(FriendlyByteBuf buf) {
        this.fogStart = buf.readFloat();
        this.fogEnd = buf.readFloat();
        this.fogRed = buf.readFloat();
        this.fogGreen = buf.readFloat();
        this.fogBlue = buf.readFloat();
        this.transition = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(fogStart);
        buf.writeFloat(fogEnd);
        buf.writeFloat(fogRed);
        buf.writeFloat(fogGreen);
        buf.writeFloat(fogBlue);
        buf.writeFloat(transition);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            FogManager.INSTANCE.setServerState(fogStart, fogEnd, fogRed, fogGreen, fogBlue, transition);
        });
        return true;
    }
}