package net.ip.zip.item;

import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class BaseThrowableItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final float shootSpeed;
    private final SimpleGeoModel<BaseThrowableItem> model;
    private static final int COOLDOWN_TICKS = 40;
    private static final int MAX_CHARGE_TICKS = 20;
    private static final int MIN_CHARGE_TICKS = 5;

    protected BaseThrowableItem(Properties properties, String modelName, float shootSpeed) {
        super(properties);
        this.shootSpeed = shootSpeed;
        this.model = new SimpleGeoModel<>("geo/throwables", modelName, "textures/entity/throwables", modelName, "animations/throwables", modelName);
    }

    protected abstract ThrowableItemProjectile createEntity(Player player, Level level);

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(item);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        if (player.getCooldowns().isOnCooldown(this)) return;
        int chargeTicks = getUseDuration(stack) - timeCharged;
        if (chargeTicks < MIN_CHARGE_TICKS) return;
        float power = Math.min(1.0f, (float) chargeTicks / MAX_CHARGE_TICKS);
        float speed = 0.5f + (shootSpeed - 0.5f) * power;
        if (!level.isClientSide) {
            ThrowableItemProjectile projectile = createEntity(player, level);
            projectile.setItem(stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, 1.0F);
            level.addFreshEntity(projectile);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) stack.shrink(1);
        applyCooldown(player);
    }

    private void applyCooldown(Player player) {
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
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
            private SimpleGeoItemRenderer<BaseThrowableItem> renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new SimpleGeoItemRenderer<>(model);
                return this.renderer;
            }
        });
    }
}
