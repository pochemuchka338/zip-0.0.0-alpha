package net.ip.zip.network.packet;

import java.util.UUID;

public class AffectedPlayerInfo {
    public final UUID uuid;
    public final StunEffectData effectData;

    public AffectedPlayerInfo(UUID uuid, StunEffectData effectData) {
        this.uuid = uuid;
        this.effectData = effectData;
    }
}