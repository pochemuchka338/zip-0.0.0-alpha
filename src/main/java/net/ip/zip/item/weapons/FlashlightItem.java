package net.ip.zip.item.weapons;

import net.ip.zip.client.renderer.FlashlightItemRenderer;
import net.ip.zip.item.GeoItem;
import net.ip.zip.item.ModItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FlashlightItem extends GeoItem {
    public static final int MAX_CHARGE = 12000;

    public java.util.Properties props;

    public FlashlightItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        int charge = tag.getInt("charge");
        ItemStack offhand = player.getOffhandItem();

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (hand == InteractionHand.MAIN_HAND && offhand.is(ModItem.Battery.get()) && charge < MAX_CHARGE) {
            if (!world.isClientSide) {
                tag.putInt("charge", Math.min(MAX_CHARGE, charge + 2000));
                if (!player.getAbilities().instabuild) {
                    offhand.shrink(1);
                }
            }
            player.getCooldowns().addCooldown(this, 5);
            return InteractionResultHolder.success(stack);
        }

        boolean isOn = tag.getBoolean("isOn");
        tag.putBoolean("isOn", !isOn);
        player.getCooldowns().addCooldown(this, 5);

        if (tag.getBoolean("isOn")) {
            if (charge <= 0) {
                tag.putBoolean("isOn", false);
            } else {
                int percent = (int) ((charge / (float) MAX_CHARGE) * 100);
                Component msg = Component.literal("Фонарь: " + percent + "%").withStyle(ChatFormatting.YELLOW);
                player.displayClientMessage(msg, true);
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResultHolder<ItemStack> result = this.use(context.getLevel(), context.getPlayer(), context.getHand());
        return result.getResult();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;
        if (stack != player.getItemInHand(InteractionHand.MAIN_HAND) && stack != player.getItemInHand(InteractionHand.OFF_HAND)) return;

        CompoundTag tag = stack.getOrCreateTag();
        boolean isOn = tag.getBoolean("isOn");
        int charge = tag.getInt("charge");

        if (isOn) {
            if (!world.isClientSide) {
                if (charge > 0) {
                    tag.putInt("charge", Math.max(0, charge - 4));
                } else {
                    tag.putBoolean("isOn", false);
                }
            }
        }
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new FlashlightItemRenderer();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) return true;
        if (oldStack.getItem() != newStack.getItem()) return true;
        CompoundTag oldTag = oldStack.getTag();
        CompoundTag newTag = newStack.getTag();
        if (oldTag == null && newTag == null) return false;
        if (oldTag == null || newTag == null) return true;
        return oldTag.getBoolean("isOn") != newTag.getBoolean("isOn");
    }
}