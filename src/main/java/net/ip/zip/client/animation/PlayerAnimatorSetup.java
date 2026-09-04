package net.ip.zip.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.ip.zip.ZIP;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlayerAnimatorSetup {
    private static final List<String> KEYS = List.of("yamato_animation", "bat_animation", "medical_animation", "machete_animation", "kitchen_knife_animation", "stop_sign_animation", "binoculars_animation");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for (String key : KEYS) {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                    new ResourceLocation(ZIP.MOD_ID, key),
                    42,
                    PlayerAnimatorSetup::registerPlayerAnimation);
        }
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }
}