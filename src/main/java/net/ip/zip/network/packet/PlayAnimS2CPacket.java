package net.ip.zip.network.packet;

import net.ip.zip.client.animation.AnimationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayAnimS2CPacket {
    private final String dataKey;
    private final String animName;
    private final boolean firstPerson;

    public PlayAnimS2CPacket(String dataKey, String animName, boolean firstPerson) {
        this.dataKey = dataKey;
        this.animName = animName;
        this.firstPerson = firstPerson;
    }

    public PlayAnimS2CPacket(FriendlyByteBuf buf) {
        this.dataKey = buf.readUtf();
        this.animName = buf.readUtf();
        this.firstPerson = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(dataKey);
        buf.writeUtf(animName);
        buf.writeBoolean(firstPerson);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                AnimationHandler.play(player, dataKey, animName, firstPerson);
            }
        });
        return true;
    }
}