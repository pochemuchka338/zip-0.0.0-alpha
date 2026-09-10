package net.ip.zip.item.weapons;

import net.ip.zip.block.ModBlocks;
import net.ip.zip.client.renderer.FlashlightItemRenderer;
import net.ip.zip.item.GeoItem;
import net.ip.zip.item.ModItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlashlightItem extends GeoItem {
    private static final double FLASHLIGHT_DISTANCE = 10.0;
    private static final int MAX_CHARGE = 12000;
    private static final Map<UUID, Set<BlockPos>> ACTIVE_LIGHTS = new ConcurrentHashMap<>();

    public FlashlightItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        int charge = tag.getInt("charge");
        ItemStack offhand = player.getOffhandItem();

        if (hand == InteractionHand.MAIN_HAND && offhand.is(ModItem.Battery.get()) && charge < MAX_CHARGE) {
            int newCharge = Math.min(charge + 2000, MAX_CHARGE);
            tag.putInt("charge", newCharge);
            offhand.shrink(1);
            return InteractionResultHolder.success(stack);
        }

        boolean isOn = tag.getBoolean("isOn");
        tag.putBoolean("isOn", !isOn);

        if (!world.isClientSide && !tag.getBoolean("isOn")) {
            clearLightsForPlayer(player, world);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof Player player)) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        boolean isOn = tag.getBoolean("isOn");
        int charge = tag.getInt("charge");

        if (!isSelected) {
            if (!world.isClientSide && isOn) {
                tag.putBoolean("isOn", false);
                clearLightsForPlayer(player, world);
            }
            return;
        }

        if (world.getGameTime() % 4 == 0) {
            if (!world.isClientSide) {
                if (isOn && charge > 0) {
                    charge = Math.max(0, charge - 4);
                    tag.putInt("charge", charge);
                    updateLights(world, player);
                } else if (isOn && charge <= 0) {
                    tag.putBoolean("isOn", false);
                    clearLightsForPlayer(player, world);
                }
            } else {
                if (isOn && charge > 0) {
                    int percent = (int) ((charge / (float) MAX_CHARGE) * 100);
                    Component msg = Component.literal("Фонарь: " + percent + "%").withStyle(ChatFormatting.YELLOW);
                    player.displayClientMessage(msg, true);
                }
            }
        }
    }

    private void updateLights(Level world, Player player) {
        Set<BlockPos> newPositions = new HashSet<>();
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getViewVector(1.0F).scale(FLASHLIGHT_DISTANCE));

        BlockHitResult result = world.clip(new ClipContext(start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        BlockPos hitPos = result.getType() == HitResult.Type.MISS ? BlockPos.containing(end) : result.getBlockPos();

        newPositions.add(hitPos);
        newPositions.add(hitPos.offset(1, 0, 0));
        newPositions.add(hitPos.offset(-1, 0, 0));
        newPositions.add(hitPos.offset(0, 1, 0));
        newPositions.add(hitPos.offset(0, -1, 0));
        newPositions.add(hitPos.offset(0, 0, 1));
        newPositions.add(hitPos.offset(0, 0, -1));

        Set<BlockPos> oldPositions = ACTIVE_LIGHTS.computeIfAbsent(player.getUUID(), k -> new HashSet<>());

        for (BlockPos pos : oldPositions) {
            if (!newPositions.contains(pos)) {
                if (world.getBlockState(pos).is(ModBlocks.FlashlightLight.get())) {
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        for (BlockPos pos : newPositions) {
            BlockState currentState = world.getBlockState(pos);
            if (currentState.isAir()) {
                world.setBlock(pos, ModBlocks.FlashlightLight.get().defaultBlockState(), 3);
            }
        }

        oldPositions.clear();
        oldPositions.addAll(newPositions);
    }

    private void clearLightsForPlayer(Player player, Level world) {
        Set<BlockPos> oldPositions = ACTIVE_LIGHTS.remove(player.getUUID());
        if (oldPositions != null) {
            for (BlockPos pos : oldPositions) {
                if (world.getBlockState(pos).is(ModBlocks.FlashlightLight.get())) {
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
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