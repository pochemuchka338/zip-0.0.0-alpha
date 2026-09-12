package net.ip.zip.network.packet;

import net.minecraft.world.phys.Vec3;

public class StunEffectData {
    public final Vec3 position;
    public final int effectAttack;
    public final int effectSustain;
    public final int effectDecay;
    public final int effectAmount;

    public StunEffectData(Vec3 position, int effectAttack, int effectSustain, int effectDecay, int effectAmount) {
        this.position = position;
        this.effectAttack = effectAttack;
        this.effectSustain = effectSustain;
        this.effectDecay = effectDecay;
        this.effectAmount = effectAmount;
    }
}