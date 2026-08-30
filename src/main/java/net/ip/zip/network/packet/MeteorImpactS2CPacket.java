package net.ip.zip.network.packet;

import net.ip.zip.client.MeteorShakeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MeteorImpactS2CPacket {
    private final double x;
    private final double y;
    private final double z;
    private final float strength;

    public MeteorImpactS2CPacket(double x, double y, double z, float strength) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.strength = strength;
    }

    public MeteorImpactS2CPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.strength = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(strength);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            MeteorShakeHandler.addImpact(x, y, z, strength);
        });
        return true;
    }
}