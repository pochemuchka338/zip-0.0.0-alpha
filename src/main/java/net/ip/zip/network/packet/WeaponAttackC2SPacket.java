package net.ip.zip.network.packet;

import net.ip.zip.ServerEvents;
import net.ip.zip.item.weapons.YamatoItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WeaponAttackC2SPacket {
    private final int targetId;
    private final boolean heavy;

    public WeaponAttackC2SPacket(int targetId, boolean heavy) {
        this.targetId = targetId;
        this.heavy = heavy;
    }

    public WeaponAttackC2SPacket(FriendlyByteBuf buf) {
        this.targetId = buf.readInt();
        this.heavy = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(targetId);
        buf.writeBoolean(heavy);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof net.minecraft.world.item.SwordItem)) return;

            if (heavy && stack.getItem() instanceof YamatoItem) {
                ServerEvents.setHeavyLock(player);
            }

            Entity target = player.level().getEntity(targetId);
            if (target == null || !target.isAlive() || player.distanceTo(target) > 4.5) return;

            float damage = 0;
            var attrs = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
            for (var attr : attrs.get(Attributes.ATTACK_DAMAGE)) {
                damage += (float) attr.getAmount();
            }
            if (heavy) damage += 4f;

            target.hurt(player.damageSources().playerAttack(player), damage);
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        });
        return true;
    }
}