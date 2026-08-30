package net.ip.zip.network.packet;

import net.ip.zip.item.weapons.YamatoItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.function.Supplier;

public class ToggleSheathC2SPacket {
    public ToggleSheathC2SPacket() {}

    public ToggleSheathC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof YamatoItem item) {
                    boolean wasSheathed = YamatoItem.isSheathed(stack);
                    YamatoItem.toggleSheath(stack);
                    if (player.level() instanceof ServerLevel serverLevel) {
                        int id = (int) GeoItem.getOrAssignId(stack, serverLevel);
                        if (wasSheathed) {
                            item.triggerAnim(player, id, "controller", "unsheath");
                        } else {
                            item.triggerAnim(player, id, "controller", "sheath");
                        }
                    }
                }
            }
        });
        return true;
    }
}