package net.ip.zip.item.weapons;

import net.ip.zip.block.ModBlocks;
import net.ip.zip.client.renderer.StopSignItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class StopSignItem extends GeoWeaponItem {
    public StopSignItem(Properties properties) {
        super(Tiers.IRON, 5, -2.6f, properties, "StopSign block", "f1a2b3c4-d5e6-7890-abcd-ef1234567895");
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            Direction face = hit.getDirection();
            BlockPos placePos = pos.relative(face);
            BlockState stateAt = level.getBlockState(placePos);
            BlockPlaceContext placeContext = new BlockPlaceContext(player, hand, stack, hit);

            if (stateAt.canBeReplaced(placeContext)) {
                BlockState signState = ModBlocks.StopSign.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, player.getDirection().getOpposite());

                if (!level.isClientSide) {
                    level.setBlock(placePos, signState, 3);
                    level.playSound(null, placePos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new StopSignItemRenderer();
    }
}