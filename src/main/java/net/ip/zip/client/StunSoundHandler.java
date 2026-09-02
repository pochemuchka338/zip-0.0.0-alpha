package net.ip.zip.client;

import net.ip.zip.ZIP;
import net.ip.zip.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, value = Dist.CLIENT)
public class StunSoundHandler {
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
        if (event.getSound() == null) return;
        if (event.getSound() == tinnitus) return;
        event.setSound(null);
    }
}