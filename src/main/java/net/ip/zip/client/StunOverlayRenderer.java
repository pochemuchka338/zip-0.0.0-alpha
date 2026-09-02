package net.ip.zip.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ip.zip.ZIP;
import net.ip.zip.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class StunOverlayRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        var effect = mc.player.getEffect(ModEffects.STUNNED.get());
        if (effect == null) return;

        int duration = effect.getDuration();
        float alpha;
        if (duration > 40) {
            alpha = 1.0f;
        } else {
            alpha = duration / 40.0f;
        }
        if (alpha <= 0.0f) return;

        int screenW = event.getWindow().getGuiScaledWidth();
        int screenH = event.getWindow().getGuiScaledHeight();
        int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

        GuiGraphics g = event.getGuiGraphics();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.fill(0, 0, screenW, screenH, color);
        RenderSystem.disableBlend();
    }
}