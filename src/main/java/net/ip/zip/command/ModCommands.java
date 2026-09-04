package net.ip.zip.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.ip.zip.ZIP;
import net.ip.zip.entity.MeteorEntity;
import net.ip.zip.entity.ModEntities;
import net.ip.zip.fog.ServerFogManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZIP.MOD_ID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        registerFog(dispatcher);
        registerMeteor(dispatcher);
    }

    private static void registerFog(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fog")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("on").executes(ctx -> {
                    ServerFogManager.active = true;
                    ctx.getSource().sendSuccess(() -> Component.literal("Туман включен"), true);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                    ServerFogManager.active = false;
                    ctx.getSource().sendSuccess(() -> Component.literal("Туман выключен"), true);
                    return 1;
                }))
                .then(Commands.literal("toggle").executes(ctx -> {
                    ServerFogManager.active = !ServerFogManager.active;
                    ctx.getSource().sendSuccess(() -> Component.literal(ServerFogManager.active ? "Туман включен" : "Туман выключен"), true);
                    return 1;
                })));
    }

    private static void registerMeteor(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("?")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> {
                            BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
                            spawnMeteor(ctx.getSource().getLevel(), pos);
                            return 1;
                        }))
                .executes(ctx -> {
                    BlockPos pos = BlockPos.containing(ctx.getSource().getPosition());
                    spawnMeteor(ctx.getSource().getLevel(), pos);
                    return 1;
                }));
    }

    private static void spawnMeteor(ServerLevel level, BlockPos pos) {
        MeteorEntity meteor = new MeteorEntity(ModEntities.METEOR.get(), level);
        meteor.setTarget(pos);
        level.addFreshEntity(meteor);
    }
}