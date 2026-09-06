package net.ip.zip.network.packet;

import net.ip.zip.client.GunClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GunRecoilS2CPacket {
    private final float pitch;
    private final float yaw;

    public GunRecoilS2CPacket(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public GunRecoilS2CPacket(FriendlyByteBuf buf) {
        this.pitch = buf.readFloat();
        this.yaw = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {
                GunClientEvents.applyRecoil(player.getUUID(), pitch, yaw);
            }
        });
        return true;
    }
}