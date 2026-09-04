package net.ip.zip.client.fog;

import net.minecraft.util.Mth;

public class InterpolatedValue {
    private float current;
    private float previous;
    private final float speed;

    public InterpolatedValue(float initial, float speed) {
        this.current = initial;
        this.previous = initial;
        this.speed = speed;
    }

    public void set(float value) {
        this.previous = this.current;
        this.current = value;
    }

    public void interpolate(float target) {
        this.previous = this.current;
        this.current = Mth.lerp(this.speed, this.current, target);
    }

    public void interpolate(float target, float customSpeed) {
        this.previous = this.current;
        this.current = Mth.lerp(customSpeed, this.current, target);
    }

    public float get(float partialTick) {
        return Mth.lerp(partialTick, previous, current);
    }

    public float getCurrent() {
        return current;
    }

    public void resetTo(float value) {
        this.current = value;
        this.previous = value;
    }
}