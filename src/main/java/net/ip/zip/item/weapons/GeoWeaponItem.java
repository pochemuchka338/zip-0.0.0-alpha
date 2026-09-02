package net.ip.zip.item.weapons;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class GeoWeaponItem extends SwordItem implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final AttributeModifier blockSlow;
    protected final boolean canBlock;

    protected GeoWeaponItem(Tiers tier, int attackDamage, float attackSpeed, Properties properties, String slowName, String slowUuid, boolean canBlock) {
        super(tier, attackDamage, attackSpeed, properties);
        this.canBlock = canBlock;
        this.blockSlow = new AttributeModifier(UUID.fromString(slowUuid), slowName, -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return canBlock ? 72000 : 0;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return canBlock ? UseAnim.BLOCK : UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (canBlock) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!canBlock) return;
        if (entity instanceof Player player && isSelected) {
            var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
                if (player.isUsingItem() && player.getUseItem() == stack) {
                    if (!attr.hasModifier(blockSlow)) {
                        attr.addTransientModifier(blockSlow);
                    }
                } else {
                    if (attr.hasModifier(blockSlow)) {
                        attr.removeModifier(blockSlow);
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

    protected abstract GeoItemRenderer<?> createRenderer();

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = createRenderer();
                return this.renderer;
            }
        });
    }
}