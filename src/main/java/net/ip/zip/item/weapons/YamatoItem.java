package net.ip.zip.item.weapons;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.ip.zip.client.renderer.YamatoItemRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class YamatoItem extends GeoWeaponItem {
    private static final String TAG_SHEATHED = "Sheathed";

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation UNSHEATH_ANIM = RawAnimation.begin().thenPlay("unsheath").thenLoop("idle");
    private static final RawAnimation SHEATH_ANIM = RawAnimation.begin().thenPlay("sheath").thenLoop("idle");

    public YamatoItem(Properties properties) {
        super(Tiers.NETHERITE, 0, -2.4f, properties.durability(1000), "Yamato block", "a1b2c3d4-e5f6-7890-abcd-ef1234567890", true);
    }

    public static boolean isSheathed(ItemStack stack) {
        if (!stack.getOrCreateTag().contains(TAG_SHEATHED)) {
            stack.getOrCreateTag().putBoolean(TAG_SHEATHED, true);
        }
        return stack.getOrCreateTag().getBoolean(TAG_SHEATHED);
    }

    public static void setSheathed(ItemStack stack, boolean sheathed) {
        stack.getOrCreateTag().putBoolean(TAG_SHEATHED, sheathed);
    }

    public static void toggleSheath(ItemStack stack) {
        setSheathed(stack, !isSheathed(stack));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.removeAll(Attributes.ATTACK_DAMAGE);
            double damage = isSheathed(stack) ? 5.0 : 9.0;
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return isSheathed(stack) ? 72000 : 0;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return isSheathed(stack) ? UseAnim.BLOCK : UseAnim.NONE;
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level level, Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isSheathed(stack)) {
            player.startUsingItem(hand);
            return net.minecraft.world.InteractionResultHolder.consume(stack);
        }
        return net.minecraft.world.InteractionResultHolder.pass(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE_ANIM))
                .triggerableAnim("unsheath", UNSHEATH_ANIM)
                .triggerableAnim("sheath", SHEATH_ANIM));
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new YamatoItemRenderer();
    }
}