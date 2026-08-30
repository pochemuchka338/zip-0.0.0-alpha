package net.ip.zip.client.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.ip.zip.ZIP;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public class AnimationHandler {
    public static void play(AbstractClientPlayer player, String dataKey, String animationName, boolean firstPerson) {
        play(player, dataKey, animationName, firstPerson, 8);
    }

    public static void play(AbstractClientPlayer player, String dataKey, String animationName, boolean firstPerson, int fadeTicks) {
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player)
                .get(new ResourceLocation(ZIP.MOD_ID, dataKey));
        if (animation == null) return;
        var anim = PlayerAnimationRegistry.getAnimation(new ResourceLocation(ZIP.MOD_ID, animationName));
        if (anim == null) return;
        KeyframeAnimationPlayer playerAnim = new KeyframeAnimationPlayer(anim);
        if (firstPerson) {
            playerAnim.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
            playerAnim.setFirstPersonConfiguration(new FirstPersonConfiguration()
                    .setShowRightArm(true)
                    .setShowLeftArm(false)
                    .setShowRightItem(true)
                    .setShowLeftItem(false));
        } else {
            playerAnim.setFirstPersonMode(FirstPersonMode.DISABLED);
        }
        animation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeTicks, Ease.OUTCUBIC), playerAnim);
    }

    public static void stop(AbstractClientPlayer player, String dataKey) {
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player)
                .get(new ResourceLocation(ZIP.MOD_ID, dataKey));
        if (animation != null) {
            animation.setAnimation(null);
        }
    }

    public static boolean isPlaying(AbstractClientPlayer player, String dataKey) {
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player)
                .get(new ResourceLocation(ZIP.MOD_ID, dataKey));
        if (animation == null) return false;
        var anim = animation.getAnimation();
        if (anim instanceof KeyframeAnimationPlayer kf) {
            return kf.isActive();
        }
        return anim != null;
    }
}