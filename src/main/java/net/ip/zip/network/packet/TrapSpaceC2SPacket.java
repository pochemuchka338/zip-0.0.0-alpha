package net.ip.zip.network.packet;

import net.ip.zip.block.entity.TrapBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TrapSpaceC2SPacket {
    public TrapSpaceC2SPacket() {
    }

    public TrapSpaceC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CompoundTag data = player.getPersistentData();
                if (data.getBoolean("inTrap") && data.contains("trapPos")) {
                    BlockPos pos = BlockPos.of(data.getLong("trapPos"));
                    if (player.level().getBlockEntity(pos) instanceof TrapBlockEntity trap) {
                        trap.playerTapSpace(player);
                    }
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}