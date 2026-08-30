package net.ip.zip.command;

import net.ip.zip.entity.MeteorEntity;
import net.ip.zip.entity.ModEntities;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MeteorCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("?")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> {
                            BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
                            ServerLevel level = ctx.getSource().getLevel();
                            MeteorEntity meteor = new MeteorEntity(ModEntities.METEOR.get(), level);
                            meteor.setTarget(pos);
                            level.addFreshEntity(meteor);
                            return 1;
                        }))
                .executes(ctx -> {
                    BlockPos pos = BlockPos.containing(ctx.getSource().getPosition());
                    ServerLevel level = ctx.getSource().getLevel();
                    MeteorEntity meteor = new MeteorEntity(ModEntities.METEOR.get(), level);
                    meteor.setTarget(pos);
                    level.addFreshEntity(meteor);
                    return 1;
                }));
    }
}