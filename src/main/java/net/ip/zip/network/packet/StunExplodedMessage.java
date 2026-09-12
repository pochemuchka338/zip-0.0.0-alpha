package net.ip.zip.network.packet;

import net.ip.zip.client.render.StunBlindEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class StunExplodedMessage {
    public final Vec3 position;
    public final List<AffectedPlayerInfo> affectedPlayers;

    public StunExplodedMessage(Vec3 position, List<AffectedPlayerInfo> affectedPlayers) {
        this.position = position;
        this.affectedPlayers = affectedPlayers;
    }

    public static void encode(StunExplodedMessage msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.position.x);
        buf.writeDouble(msg.position.y);
        buf.writeDouble(msg.position.z);

        buf.writeInt(msg.affectedPlayers.size());
        for (AffectedPlayerInfo info : msg.affectedPlayers) {
            buf.writeUUID(info.uuid);
            buf.writeDouble(info.effectData.position.x);
            buf.writeDouble(info.effectData.position.y);
            buf.writeDouble(info.effectData.position.z);
            buf.writeInt(info.effectData.effectAttack);
            buf.writeInt(info.effectData.effectSustain);
            buf.writeInt(info.effectData.effectDecay);
            buf.writeInt(info.effectData.effectAmount);
        }
    }

    public static StunExplodedMessage decode(FriendlyByteBuf buf) {
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int size = buf.readInt();
        List<AffectedPlayerInfo> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            Vec3 ePos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            int attack = buf.readInt();
            int sustain = buf.readInt();
            int decay = buf.readInt();
            int amount = buf.readInt();

            list.add(new AffectedPlayerInfo(uuid, new StunEffectData(ePos, attack, sustain, decay, amount)));
        }
        return new StunExplodedMessage(pos, list);
    }

    public static void handle(StunExplodedMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return;

            System.out.println("[ZIP MOD] Пакет взрыва получен! Пострадавших игроков вокруг: " + msg.affectedPlayers.size());

            for (AffectedPlayerInfo info : msg.affectedPlayers) {
                if (info.uuid.equals(player.getUUID())) {
                    System.out.println("[ZIP MOD] Вы были ослеплены! Время удержания: " + info.effectData.effectSustain + "мс");
                    StunBlindEffectRenderer.INSTANCE.render(info.effectData);
                }
            }

            player.level().playLocalSound(msg.position.x, msg.position.y, msg.position.z,
                    SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 2.0f, 1.5f, false);
        });
        ctx.get().setPacketHandled(true);
    }
}
