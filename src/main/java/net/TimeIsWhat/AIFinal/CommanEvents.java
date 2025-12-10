package net.TimeIsWhat.AIFinal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = AiFinal.MOD_ID) // server-side subscriber
public class CommanEvents {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("playerscore")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    PlayerStats stats = PlayerEventHandler.getStatsMap().get(player.getUUID());
                    double score = stats.get_playerScore();

                    context.getSource().sendSuccess(
                            () -> Component.literal("Your player score is: " + score),
                            false
                    );
                    return 1;
                })
        );

        dispatcher.register(Commands.literal("clear_deaths")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    PlayerStats stats = PlayerEventHandler.getStatsMap().get(player.getUUID());
                    stats.setDeaths(0);

                    context.getSource().sendSuccess(
                            () -> Component.literal("Deaths cleared"),
                            false
                    );
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("clear_closeCalls")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    PlayerStats stats = PlayerEventHandler.getStatsMap().get(player.getUUID());
                    stats.setNumberOfCloseCalls(0);

                    context.getSource().sendSuccess(
                            () -> Component.literal("close calls cleared"),
                            false
                    );
                    return 1;
                })
        );
    }
}