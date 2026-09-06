package net.ip.zip.item.weapons;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class BaseGunItem extends Item implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final GunStats stats;

    public BaseGunItem(GunStats stats, Properties properties) {
        super(properties);
        this.stats = stats;
    }

    public GunStats getStats() {
        return stats;
    }

    public int getAmmo(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Ammo");
    }

    public void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt("Ammo", Math.max(0, Math.min(ammo, stats.magazineSize())));
    }

    public boolean isFullAuto(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("FullAuto");
    }

    public void setFullAuto(ItemStack stack, boolean auto) {
        stack.getOrCreateTag().putBoolean("FullAuto", auto);
    }

    public boolean isReloading(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Reloading");
    }

    public void setReloading(ItemStack stack, boolean reloading) {
        stack.getOrCreateTag().putBoolean("Reloading", reloading);
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
                if (this.renderer == null) this.renderer = createRenderer();
                return this.renderer;
            }
        });
    }
}