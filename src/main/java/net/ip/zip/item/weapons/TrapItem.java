package net.ip.zip.item.weapons;

import net.ip.zip.block.ModBlocks;
import net.ip.zip.client.model.SimpleGeoModel;
import net.ip.zip.client.renderer.SimpleGeoItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TrapItem extends GeoWeaponItem {

    public TrapItem(Properties properties) {
        super(Tiers.IRON, 5, -3.0f, properties, "trap_block_slow", "a3f7c9e1-4b2d-4f8a-9e6c-1d5b8a3f7c9e", true);
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
                // Теперь абсолютно точно так же, как у знака СТОП:
                BlockState trapState = ModBlocks.Trap.get().defaultBlockState();
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