package net.ip.zip.item;

import net.ip.zip.client.animation.AnimationHandler;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.ip.zip.client.model.MedicalItemModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class MedicalItem extends Item implements GeoItem {
    private final String modelName;
    private final String animName;
    private final float healAmount;
    private final int useDuration;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String DATA_KEY = "medical_animation";
    private static final UUID BLOCK_SLOW_ID = UUID.fromString("c1d2e3f4-a5b6-7890-abcd-ef1234567892");
    private static final AttributeModifier BLOCK_SLOW = new AttributeModifier(BLOCK_SLOW_ID, "Medical block", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public MedicalItem(String modelName, String animName, float healAmount, int useDuration, Properties properties) {
        super(properties);
        this.modelName = modelName;
        this.animName = animName;
        this.healAmount = healAmount;
        this.useDuration = useDuration;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public String getModelName() {
        return modelName;
    }

    public int getUseDuration() {
        return useDuration;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return useDuration;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getHealth() >= player.getMaxHealth()) {
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(hand);
        if (level.isClientSide()) {
            AnimationHandler.play((AbstractClientPlayer) player, DATA_KEY, animName, true);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (level.isClientSide() && entity instanceof AbstractClientPlayer player) {
            AnimationHandler.stop(player, DATA_KEY);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            player.heal(healAmount);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        if (level.isClientSide() && entity instanceof AbstractClientPlayer player) {
            AnimationHandler.stop(player, DATA_KEY);
        }
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof Player player && isSelected) {
            var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
                if (player.isUsingItem() && player.getUseItem() == stack) {
                    if (!attr.hasModifier(BLOCK_SLOW)) {
                        attr.addTransientModifier(BLOCK_SLOW);
                    }
                } else {
                    if (attr.hasModifier(BLOCK_SLOW)) {
                        attr.removeModifier(BLOCK_SLOW);
                    }
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new SimpleGeoItemRenderer<>(new MedicalItemModel());
                return this.renderer;
            }
        });
    }
}