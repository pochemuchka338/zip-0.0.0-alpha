package net.ip.zip.item.weapons;

import net.ip.zip.block.ModBlocks;
import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.ip.zip.item.GeoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TrapItem extends GeoItem {

    public TrapItem(Properties properties) {
        super(properties);
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
                BlockState trapState = ModBlocks.Trap.get().defaultBlockState()
                        .setValue(HorizontalDirectionalBlock.FACING, player.getDirection().getOpposite());
                if (!level.isClientSide()) {
                    level.setBlock(placePos, trapState, 3);
                    level.playSound(null, placePos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new SimpleGeoItemRenderer<>(SimpleGeoModel.weapon("trap"));
    }
}