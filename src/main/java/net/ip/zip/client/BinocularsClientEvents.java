package net.ip.zip.client;

import net.ip.zip.ZIP;
import net.ip.zip.client.animation.AnimationHandler;
import net.ip.zip.item.BinocularsItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class BinocularsClientEvents {
    private static final Map<UUID, Boolean> wasUsing = new HashMap<>();
    private static final String DATA_KEY = "binoculars_animation";
    private static final float[] ZOOM_LEVELS = {2.0f, 4.0f, 8.0f, 12.0f, 16.0f};
    private static final String[] ZOOM_SUFFIXES = {"2x", "4x", "8x", "12x", "16x"};
    private static int zoomIndex = 0;
    private static double savedSensitivity = -1.0D;
    private static boolean zooming = false;
    private static final int OVERLAY_TEX_SIZE = 512;

    public static int getZoomIndex() {
        return zoomIndex;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        UUID uuid = mc.player.getUUID();
        boolean using = mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof BinocularsItem;
        boolean was = wasUsing.getOrDefault(uuid, false);

        if (using && !was) {
            AnimationHandler.play(mc.player, DATA_KEY, "binoculars_use", true);
            savedSensitivity = mc.options.sensitivity().get();
            zoomIndex = 0;
            zooming = true;
        } else if (!using && was) {
            AnimationHandler.stop(mc.player, DATA_KEY);
            if (savedSensitivity >= 0.0D) {
                mc.options.sensitivity().set(savedSensitivity);
                savedSensitivity = -1.0D;
            }
            zooming = false;
        }

        if (using && savedSensitivity >= 0.0D) {
            mc.options.sensitivity().set(savedSensitivity / ZOOM_LEVELS[zoomIndex]);
        }
        wasUsing.put(uuid, using);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof BinocularsItem) {
            event.setFOV(event.getFOV() / ZOOM_LEVELS[zoomIndex]);
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (!zooming) return;
        event.setCanceled(true);
        double delta = event.getScrollDelta();
        if (delta > 0.0D && zoomIndex < ZOOM_LEVELS.length - 1) {
            zoomIndex++;
        } else if (delta < 0.0D && zoomIndex > 0) {
            zoomIndex--;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof BinocularsItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof BinocularsItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!mc.player.isUsingItem() || !(mc.player.getUseItem().getItem() instanceof BinocularsItem)) return;

        int screenW = event.getWindow().getGuiScaledWidth();
        int screenH = event.getWindow().getGuiScaledHeight();
        String suffix = ZOOM_SUFFIXES[Math.min(zoomIndex, ZOOM_SUFFIXES.length - 1)];
        ResourceLocation overlay = new ResourceLocation(ZIP.MOD_ID, "textures/gui/binoculars_overlay_" + suffix + ".png");
        int texW = OVERLAY_TEX_SIZE;
        int texH = OVERLAY_TEX_SIZE;
        float scale = Math.min((float) screenW / texW, (float) screenH / texH);
        int drawW = (int) (texW * scale);
        int drawH = (int) (texH * scale);
        int x = (screenW - drawW) / 2;
        int y = (screenH - drawH) / 2;

        GuiGraphics g = event.getGuiGraphics();
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1);
        g.blit(overlay, 0, 0, 0, 0, texW, texH, texW, texH);
        g.pose().popPose();

        if (x > 0) {
            g.fill(0, 0, x, screenH, 0xFF000000);
            g.fill(x + drawW, 0, screenW, screenH, 0xFF000000);
        }
        if (y > 0) {
            g.fill(x, 0, x + drawW, y, 0xFF000000);
            g.fill(x, y + drawH, x + drawW, screenH, 0xFF000000);
        }
    }
}