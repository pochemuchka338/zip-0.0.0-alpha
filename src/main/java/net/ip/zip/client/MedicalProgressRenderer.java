package net.ip.zip.client;

import net.ip.zip.ZIP;
import net.ip.zip.item.MedicalItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class MedicalProgressRenderer {
    private static final ResourceLocation BG = new ResourceLocation(ZIP.MOD_ID, "textures/gui/medical_progress_bg.png");
    private static final ResourceLocation FILL = new ResourceLocation(ZIP.MOD_ID, "textures/gui/medical_progress_fill.png");
    private static final int TEX_W = 30;
    private static final int TEX_H = 2;
    private static final int SCALE = 2;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isUsingItem()) return;

        ItemStack stack = mc.player.getUseItem();
        if (!(stack.getItem() instanceof MedicalItem item)) return;

        int useDuration = item.getUseDuration();
        int remaining = mc.player.getUseItemRemainingTicks();
        if (remaining <= 0 || remaining > useDuration) return;

        float progress = Math.min(1.0f, (float) (useDuration - remaining) / (float) useDuration);

        int screenW = event.getWindow().getGuiScaledWidth();
        int screenH = event.getWindow().getGuiScaledHeight();
        int barW = TEX_W * SCALE;
        int barH = TEX_H * SCALE;
        int x = screenW / 2 - barW / 2;
        int y = screenH / 2 + 14;

        GuiGraphics g = event.getGuiGraphics();

        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(SCALE, SCALE, 1);
        g.blit(BG, 0, 0, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
        g.pose().popPose();

        int fillW = (int) (TEX_W * progress);
        if (fillW > 0) {
            g.pose().pushPose();
            g.pose().translate(x, y, 0);
            g.pose().scale(SCALE, SCALE, 1);
            g.blit(FILL, 0, 0, 0, 0, fillW, TEX_H, TEX_W, TEX_H);
            g.pose().popPose();
        }
    }
}