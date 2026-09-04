package net.ip.zip.client.fog;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = net.ip.zip.ZIP.MOD_ID, value = Dist.CLIENT)
public class FogClientEvents {
    private static float clientSuffocationTimer = 0.0f;

    public static void setSuffocationTimer(float timer) {
        clientSuffocationTimer = timer;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FogManager.INSTANCE.update();
            updateSuffocationEffects();
        }
    }

    private static void updateSuffocationEffects() {
        if (clientSuffocationTimer <= 0.0f) {
            onSuffocationClear();
            return;
        }
        if (clientSuffocationTimer >= 25.0f) {
            onCriticalSuffocation(clientSuffocationTimer);
        } else if (clientSuffocationTimer > 15.0f) {
            onHeavySuffocation(clientSuffocationTimer);
        } else if (clientSuffocationTimer > 5.0f) {
            onLightSuffocation(clientSuffocationTimer);
        }
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;
        if (mc.gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE) return;

        FogSettings s = FogManager.INSTANCE.getSettings((float) event.getPartialTick(),
                mc.gameRenderer.getRenderDistance() * 16.0f);
        event.setRed(s.fogRed());
        event.setGreen(s.fogGreen());
        event.setBlue(s.fogBlue());
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;
        if (mc.gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE) return;

        FogSettings s = FogManager.INSTANCE.getSettings((float) event.getPartialTick(), event.getFarPlaneDistance());
        event.setNearPlaneDistance(s.fogStart());
        event.setFarPlaneDistance(s.fogEnd());
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        if (FogManager.INSTANCE.transition <= 0.01f) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float alpha = FogManager.INSTANCE.transition * 0.75f;
        FogSettings s = FogManager.INSTANCE.getSettings(event.getPartialTick(), 1.0f);

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

        buffer.vertex(-1, -1, 1).color(s.fogRed(), s.fogGreen(), s.fogBlue(), alpha).endVertex();
        buffer.vertex(1, -1, 1).color(s.fogRed(), s.fogGreen(), s.fogBlue(), alpha).endVertex();
        buffer.vertex(1, 1, 1).color(s.fogRed(), s.fogGreen(), s.fogBlue(), alpha).endVertex();
        buffer.vertex(-1, 1, 1).color(s.fogRed(), s.fogGreen(), s.fogBlue(), alpha).endVertex();

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setProjectionMatrix(oldProj, oldSort);
    }

    public static void onLightSuffocation(float timer) {}
    public static void onHeavySuffocation(float timer) {}
    public static void onCriticalSuffocation(float timer) {}
    public static void onSuffocationClear() {}
}