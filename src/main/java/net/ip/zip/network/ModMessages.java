package net.ip.zip.network;

import net.ip.zip.ZIP;
import net.ip.zip.network.packet.FogStateS2CPacket;
import net.ip.zip.network.packet.MeteorImpactS2CPacket;
import net.ip.zip.network.packet.PlayAnimS2CPacket;
import net.ip.zip.network.packet.SuffocationTimerS2CPacket;
import net.ip.zip.network.packet.ToggleSheathC2SPacket;
import net.ip.zip.network.packet.WeaponAttackC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ZIP.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ToggleSheathC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleSheathC2SPacket::new)
                .encoder(ToggleSheathC2SPacket::toBytes)
                .consumerMainThread(ToggleSheathC2SPacket::handle)
                .add();

        net.messageBuilder(WeaponAttackC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(WeaponAttackC2SPacket::new)
                .encoder(WeaponAttackC2SPacket::toBytes)
                .consumerMainThread(WeaponAttackC2SPacket::handle)
                .add();

        net.messageBuilder(PlayAnimS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayAnimS2CPacket::new)
                .encoder(PlayAnimS2CPacket::toBytes)
                .consumerMainThread(PlayAnimS2CPacket::handle)
                .add();

        net.messageBuilder(MeteorImpactS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MeteorImpactS2CPacket::new)
                .encoder(MeteorImpactS2CPacket::toBytes)
                .consumerMainThread(MeteorImpactS2CPacket::handle)
                .add();

        net.messageBuilder(FogStateS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(FogStateS2CPacket::new)
                .encoder(FogStateS2CPacket::toBytes)
                .consumerMainThread(FogStateS2CPacket::handle)
                .add();

        net.messageBuilder(SuffocationTimerS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SuffocationTimerS2CPacket::new)
                .encoder(SuffocationTimerS2CPacket::toBytes)
                .consumerMainThread(SuffocationTimerS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToPlayersNear(MSG message, ServerLevel level, double x, double y, double z, double radius) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level.dimension())), message);
    }

    public static <MSG> void sendToPlayersInDimension(MSG message, ServerLevel level) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    }
}