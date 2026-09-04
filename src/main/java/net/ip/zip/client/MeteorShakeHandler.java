package net.ip.zip.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.ip.zip.ZIP;
import net.ip.zip.entity.MeteorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class MeteorShakeHandler {
    private static final List<Impact> impacts = new ArrayList<>();

    public static void addImpact(double x, double y, double z, float strength) {
        impacts.add(new Impact(x, y, z, strength));
    }

    private static float calculateRedness(Minecraft mc) {
        if (mc.player == null || mc.level == null) return 0;
        float maxIntensity = 0;
        for (MeteorEntity meteor : mc.level.getEntitiesOfClass(MeteorEntity.class, mc.player.getBoundingBox().inflate(1024))) {
            if (!meteor.isAlive()) continue;
            double dist = mc.player.distanceTo(meteor);
            if (dist > 1024) continue;
            double y = meteor.getY();
            double heightProgress = 1.0 - Math.max(0, Math.min(1, (y - 50) / 550.0));
            double distFactor = Math.max(0, 1.0 - dist / 1024.0);
            maxIntensity = Math.max(maxIntensity, (float) (heightProgress * distFactor));
        }
        return maxIntensity;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        Minecraft mc = Minecraft.getInstance();
        float intensity = calculateRedness(mc);
        if (intensity <= 0.01f) return;
        Matrix4f oldProj = RenderSystem.getProjectionMatrix();
        VertexSorting oldSort = RenderSystem.getVertexSorting();
        Matrix4f ortho = new Matrix4f().setOrtho(-1, 1, -1, 1, -1, 1);
        RenderSystem.setProjectionMatrix(ortho, VertexSorting.byDistance(0, 0, 0));
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float r = intensity * 0.9f;
        float g = intensity * 0.15f;
        float b = 0;
        float a = intensity * 0.55f;
        for (int i = 0; i < 4; i++) {
            float x = (i == 0 || i == 3) ? -1 : 1;
            float y = (i == 0 || i == 1) ? -1 : 1;
            buffer.vertex(x, y, 1).color(r, g, b, a).endVertex();
        }
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setProjectionMatrix(oldProj, oldSort);
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        float[] shake = {0, 0, 0};
        double maxDist = 1024.0;
        float maxIntensity = 3.5f;
        float time = (float) (mc.level.getGameTime() + event.getPartialTick());

        for (MeteorEntity meteor : mc.level.getEntitiesOfClass(MeteorEntity.class, mc.player.getBoundingBox().inflate(maxDist))) {
            if (!meteor.isAlive()) continue;
            double dist = mc.player.distanceTo(meteor);
            if (dist > maxDist) continue;
            float factor = (float) Math.pow(1.0 - dist / maxDist, 2);
            float intensity = factor * maxIntensity;
            shake[0] += (float) Math.sin(time * 18) * intensity * 0.4f;
            shake[1] += (float) Math.cos(time * 14) * intensity * 0.4f;
            shake[2] += (float) Math.sin(time * 22) * intensity * 0.25f;
        }

        Iterator<Impact> it = impacts.iterator();
        while (it.hasNext()) {
            Impact imp = it.next();
            double dist = Math.sqrt(mc.player.distanceToSqr(imp.x, imp.y, imp.z));
            double maxImpactDist = 256.0;
            if (dist < maxImpactDist) {
                float distFactor = (float) Math.pow(1.0 - dist / maxImpactDist, 2);
                float impactIntensity = imp.strength * distFactor * (dist < 16.0 ? 2.0f : 1.0f);
                shake[0] += (mc.level.random.nextFloat() - 0.5f) * impactIntensity * 2.0f;
                shake[1] += (mc.level.random.nextFloat() - 0.5f) * impactIntensity * 2.0f;
                shake[2] += (mc.level.random.nextFloat() - 0.5f) * impactIntensity * 1.5f;
            }
            imp.strength *= 0.88f;
            if (imp.strength < 0.1f) it.remove();
        }

        event.setYaw(event.getYaw() + shake[0]);
        event.setPitch(event.getPitch() + shake[1]);
        event.setRoll(event.getRoll() + shake[2]);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        float maxIntensity = calculateRedness(mc);
        if (maxIntensity > 0) {
            event.setRed(Math.min(1.0f, event.getRed() + maxIntensity * 0.6f));
            event.setGreen(Math.max(0.0f, event.getGreen() - maxIntensity * 0.25f));
            event.setBlue(Math.max(0.0f, event.getBlue() - maxIntensity * 0.45f));
        }
    }

    private static class Impact {
        double x, y, z;
        float strength;
        Impact(double x, double y, double z, float strength) {
            this.x = x; this.y = y; this.z = z; this.strength = strength;
        }
    }
}