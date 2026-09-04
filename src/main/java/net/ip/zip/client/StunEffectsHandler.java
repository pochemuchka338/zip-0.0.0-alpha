package net.ip.zip.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ip.zip.ZIP;
import net.ip.zip.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class StunEffectsHandler {
    private static SimpleSoundInstance tinnitus;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        boolean stunned = mc.player.hasEffect(ModEffects.STUNNED.get());
        if (stunned) {
            if (tinnitus == null || !mc.getSoundManager().isActive(tinnitus)) {
                tinnitus = new SimpleSoundInstance(SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.MASTER, 0.8f, 1.8f, SoundInstance.createUnseededRandom(), 0, 0, 0);
                mc.getSoundManager().play(tinnitus);
            }
        } else {
            if (tinnitus != null) {
                mc.getSoundManager().stop(tinnitus);
                tinnitus = null;
            }
        }
    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!mc.player.hasEffect(ModEffects.STUNNED.get())) return;
        if (event.getSound() == null || event.getSound() == tinnitus) return;
        event.setSound(null);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        var effect = mc.player.getEffect(ModEffects.STUNNED.get());
        if (effect == null) return;
        int duration = effect.getDuration();
        float alpha = duration > 40 ? 1.0f : duration / 40.0f;
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