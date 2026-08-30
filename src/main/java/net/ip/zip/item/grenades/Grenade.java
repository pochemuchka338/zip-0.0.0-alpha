package net.ip.zip.item.grenades;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Grenade extends Item {
    public Grenade(Properties pProperties) {
        super(pProperties);
    }

    //override

//    @Override
//    public InteractionResult useOn(UseOnContext pContext) {
//        if (!pContext.getLevel().isClientSide()) {
//            BlockPos positionCliked = pContext.getClickedPos();
//            Player player = pContext.getPlayer();
//            boolean foundBlock = false;
//
//            for (int i = 0; i < positionCliked.getY() + 64; i++) {
//                BlockState state = pContext.getLevel().getBlockState(positionCliked.below(i));
//
//                if (isValuableBlock(state)) {
//                    outputValuableCoordinates(positionCliked.below(i), player, state.getBlock());
//                    foundBlock = true;
//
//                    break;
//                }
//            }
//
//            if (!foundBlock) {
//                player.sendSystemMessage(Component.literal("No"));
//            }
//        }
//
//        pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(),
//                player -> player.broadcastBreakEvent(player.getUsedItemHand()));
//
//        return InteractionResult.SUCCESS;
//    }
//
//    private void outputValuableCoordinates(BlockPos blockPos, Player player, Block block) {
//        player.sendSystemMessage(Component.literal("Found " + I18n.get(block.getDescriptionId()) + " at " +
//                "(" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + ")"));
//    }
//
//    private boolean isValuableBlock(BlockState state) {
//        return state.is(Blocks.IRON_ORE) || state.is(Blocks.DIAMOND_ORE);
//    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack item = player.getItemInHand(hand);
    level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            Snowball snowball = new Snowball(level, player);
            snowball.setItem(item);
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(snowball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            item.shrink(1);
        }

    return InteractionResultHolder.sidedSuccess(item, level.isClientSide());
    }
}
