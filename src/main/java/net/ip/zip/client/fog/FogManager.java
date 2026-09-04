package net.ip.zip.client.fog;

public class FogManager {
    public static final FogManager INSTANCE = new FogManager();
    public boolean active = false;
    public float transition = 0.0f;

    private float serverStart = 0.1f;
    private float serverEnd = 0.5f;

    private float serverRed = 0.75f;
    private float serverGreen = 0.75f;
    private float serverBlue = 0.78f;

    private final InterpolatedValue fogStart = new InterpolatedValue(0.1f, 0.1f);
    private final InterpolatedValue fogEnd = new InterpolatedValue(0.5f, 0.1f);
    private final InterpolatedValue fogRed = new InterpolatedValue(0.75f, 0.1f);
    private final InterpolatedValue fogGreen = new InterpolatedValue(0.75f, 0.1f);
    private final InterpolatedValue fogBlue = new InterpolatedValue(0.78f, 0.1f);

    public void setServerState(float start, float end, float r, float g, float b, float trans) {
        this.active = trans > 0.0f || fogEnd.getCurrent() < 0.4f;
        this.transition = trans;
        this.serverStart = start;
        this.serverEnd = end;
        this.serverRed = r;
        this.serverGreen = g;
        this.serverBlue = b;
    }

    public void update() {
        fogStart.interpolate(serverStart);
        fogEnd.interpolate(serverEnd);
        fogRed.interpolate(serverRed);
        fogGreen.interpolate(serverGreen);
        fogBlue.interpolate(serverBlue);
    }

    public FogSettings getSettings(float tickDelta, float viewDistance) {
        float start = fogStart.get(tickDelta) * viewDistance;
        float end = fogEnd.get(tickDelta) * viewDistance;
        float r = fogRed.get(tickDelta);
        float g = fogGreen.get(tickDelta);
        float b = fogBlue.get(tickDelta);
        return new FogSettings(start, end, r, g, b);
    }
}