package net.TimeIsWhat.AIFinal;

import com.mojang.brigadier.Command;
import com.mojang.logging.LogUtils;
import net.TimeIsWhat.AIFinal.waves.WaveManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

import net.minecraft.world.phys.Vec3;


@Mod.EventBusSubscriber(modid = AiFinal.MOD_ID) // server-side subscriber
public class CommanEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final WaveManager WAVE_MANAGER = new WaveManager();

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


        dispatcher.register(Commands.literal("spawnwave")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    Map<UUID, PlayerStats> map = PlayerEventHandler.getStatsMap();
                    PlayerStats stats = map.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
                    double playerScore = stats.get_playerScore();

                    player.sendSystemMessage(Component.literal("Should spawn"));

                    /// calculates what mobs to spawn
                    Map<EntityType<?>, Integer> spawns = RaidRuleEngine.calculateWaveSpawns(playerScore);
                    LOGGER.info("Calculated spawn plan: {}", spawns);
                    /// getting player look and location
                    Vec3 look = player.getLookAngle();
                    ServerLevel level = player.level();
                    /// calculating where to spawn the mobs
                    int targetX = player.blockPosition().getX() + (int)(look.x * 20);
                    int targetZ = player.blockPosition().getZ() + (int)(look.z * 20);
                    int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetX, targetZ);
                    BlockPos spawnPos = new BlockPos(targetX, groundY, targetZ);
                    LOGGER.info("Spawning wave at ground position {}", spawnPos);

                    /// spawning all mobs in the map
                    spawns.forEach((mobType, count) ->
                    {
                        for (int i = 0; i < count; i++) {
                            BlockPos offsetPos = spawnPos.offset(level.random.nextInt(5) - 2, 0, level.random.nextInt(5) - 2);

                            Entity mob = mobType.spawn(
                                    level,
                                    null,
                                    null,
                                    offsetPos,
                                    EntitySpawnReason.COMMAND,
                                    true,
                                    false
                            );
                            if (mob != null) {
                                LOGGER.info("Spawned {} at {}", mob.getType().toShortString(), offsetPos);
                            } else {
                                LOGGER.warn("Failed to spawn entity of type {}", mobType.toShortString());
                            }
                        }
                    });
                    return Command.SINGLE_SUCCESS;
                })
        );


        dispatcher.register(Commands.literal("startwaves")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {

                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ServerLevel level = player.level();

                    //  Load PlayerStats
                    Map<UUID, PlayerStats> map = PlayerEventHandler.getStatsMap();
                    PlayerStats stats = map.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
                    double playerScore = stats.get_playerScore();

                    //  Start the wave system
                    WaveManager wm = WaveManager.get(level);

                    if (wm.isRunning()) {
                        player.sendSystemMessage(Component.literal("Waves are already running."));
                        return Command.SINGLE_SUCCESS;
                    }

                    wm.starWaves(level, player);
                    LOGGER.info("WaveManager instance: {}", System.identityHashCode(wm));
                    player.sendSystemMessage(Component.literal("Wave system started!"));
                    return Command.SINGLE_SUCCESS;
                })
        );

        dispatcher.register(Commands.literal("stopwaves")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ServerLevel level = player.level();
                    WaveManager.get(level).stopWaves();
                    context.getSource().sendSuccess(() -> Component.literal("Waves stopped."), false);
                    return Command.SINGLE_SUCCESS;
                })
        );


    }
}