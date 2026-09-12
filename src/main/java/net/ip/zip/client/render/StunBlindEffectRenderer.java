package net.ip.zip.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ip.zip.network.packet.StunEffectData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.time.Duration;
import java.time.Instant;

public class StunBlindEffectRenderer {
    public static final StunBlindEffectRenderer INSTANCE = new StunBlindEffectRenderer();

    private int effectAttack = 0;
    private int effectSustain = 0;
    private int effectDecay = 0;
    private int effectAmount = 0;
    private Instant renderStartTime = Instant.now();
    private int renderState = 0;
    private SoundInstance tinnitus;
    private boolean isRegistered = false;
    private StunBlindEffectRenderer() {}

    public void render(StunEffectData data) {
        if (renderState == 0) {
            renderStartTime = Instant.now();
        } else if (renderState == 1) {
            renderStartTime = Instant.now().minusMillis(data.effectAttack);
        }

        effectAttack = Math.max(effectAttack, data.effectAttack);
        effectSustain = Math.max(effectSustain, data.effectSustain);
        effectDecay = Math.max(effectDecay, data.effectDecay);
        effectAmount = Math.max(effectAmount, data.effectAmount);

        playRingSound(data);

        if (!isRegistered) {
            MinecraftForge.EVENT_BUS.register(this);
            isRegistered = true;
        }
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        if (renderState != 0) {
            SoundInstance sound = event.getSound();
            if (sound != null && sound != tinnitus) {
                if (!sound.getLocation().equals(SoundEvents.GENERIC_EXPLODE.getLocation())) {
                    event.setSound(null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.isSpectator()) {
                double timeDelta = Duration.between(renderStartTime, Instant.now()).toMillis();
                GuiGraphics guiGraphics = event.getGuiGraphics();

                int width = event.getWindow().getGuiScaledWidth();
                int height = event.getWindow().getGuiScaledHeight();

                if (timeDelta < effectAttack) {
                    int opacity = (int) (timeDelta / (double) effectAttack * effectAmount);
                    drawOverlay(guiGraphics, width, height, opacity);
                    renderState = 1;
                } else if (timeDelta < effectSustain + effectAttack) {
                    drawOverlay(guiGraphics, width, height, effectAmount);
                    renderState = 2;
                } else if (timeDelta < effectDecay + effectSustain + effectAttack) {
                    int opacity = effectAmount - (int) ((timeDelta - effectAttack - effectSustain) / (double) effectDecay * effectAmount);
                    drawOverlay(guiGraphics, width, height, opacity);
                    renderState = 3;
                } else {
                    clean();
                }
            }
        }
    }

    private void drawOverlay(GuiGraphics guiGraphics, int width, int height, int opacity) {
        if (opacity <= 0) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int color = (opacity << 24) | 0x00FFFFFF;
        guiGraphics.fill(0, 0, width, height, color);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void clean() {
        effectAttack = 0;
        effectDecay = 0;
        effectSustain = 0;
        effectAmount = 0;
        renderState = 0;
        if (tinnitus != null) {
            Minecraft.getInstance().getSoundManager().stop(tinnitus);
            tinnitus = null;
        }

        if (isRegistered) {
            MinecraftForge.EVENT_BUS.unregister(this);
            isRegistered = false;
        }
    }

    private void playRingSound(StunEffectData data) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        double distance = Math.sqrt(player.distanceToSqr(data.position));
        float volume = (float) Math.max(0.0, 1.0 - (distance / 15.0));

        if (tinnitus != null) {
            Minecraft.getInstance().getSoundManager().stop(tinnitus);
        }

        tinnitus = new SimpleSoundInstance(
                SoundEvents.NOTE_BLOCK_CHIME.value().getLocation(),
                SoundSource.MASTER,
                volume,
                1.8f,
                SoundInstance.createUnseededRandom(),
                false,
                0,
                SoundInstance.Attenuation.NONE,
                player.getX(),
                player.getY(),
                player.getZ(),
                true
        );
        Minecraft.getInstance().getSoundManager().play(tinnitus);
    }
}
