package net.TimeIsWhat.AIFinal.waves;

import net.TimeIsWhat.AIFinal.PlayerEventHandler;
import net.TimeIsWhat.AIFinal.PlayerStats;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;


import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.mojang.text2speech.Narrator.LOGGER;
public class WaveManager {

    private boolean running = false;
    private int waveNumber = 0;
    private double difficulty = 1.0;
    private UUID activePlayerId;
    private ServerPlayer player;

    private final WaveTracker tracker = new WaveTracker();

    private static final Map<ServerLevel, WaveManager> INSTANCES = new java.util.HashMap<>();

    public static WaveManager get(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level, l -> new WaveManager());
    }

    public final WaveTracker getTracker()
    {
        return tracker;
    }

    public boolean isRunning() { return running; }

    public void stopWaves() {
        LOGGER.info("[WaveManager] Waves stopped manually.");
        running = false;
    }

    public void starWaves(ServerLevel level, ServerPlayer player) {
        if (running) {
            LOGGER.warn("[WaveManager] Tried to start waves but waves are already running.");
            return;
        }
        LOGGER.info("[WaveManager] Starting waves for player {}", player.getName().getString());

        running = true;
        waveNumber = 1;
        this.player = player;
        this.activePlayerId = player.getUUID();

        runWaveLoop(level, player);
    }

    private void runWaveLoop(ServerLevel level, ServerPlayer player) {
        if (!running) {
            LOGGER.warn("[WaveManager] runWaveLoop called but waves are not running.");
            return;
        }

        LOGGER.info("[WaveManager] --- Starting Wave {} ---", waveNumber);

        tracker.reset();
        LOGGER.info("[WaveManager] Tracker reset for new wave.");

        PlayerStats stats = PlayerEventHandler.getStatsMap().get(activePlayerId);
        if (stats == null) {
            LOGGER.error("[WaveManager] ERROR: No PlayerStats found for player {}", player.getName().getString());
            return;
        }

        LOGGER.info("[WaveManager] PlayerScore = {}", stats.get_playerScore());

        double score = stats.get_playerScore();
        int deaths = stats.get_deaths();
        int closeCalls = stats.get_numberOfCloseCalls();

        WaveSpawn spawn = new WaveSpawn(difficulty, score, deaths, closeCalls);
        difficulty = spawn.adjustDifficulty(difficulty, deaths, closeCalls);
        Map<EntityType<?>, Integer> spawns = WaveSpawn.calculateWave(difficulty, score);

        LOGGER.info("[WaveManager] Wave {} difficulty {} spawn plan: {}", waveNumber, difficulty, spawns);

        spawnWave(level, player, spawns);
    }

    private static final Set<Class<?>> RAID_MOBS = Set.of(
            Pillager.class,
            Vindicator.class,
            Evoker.class,
            Ravager.class,
            Witch.class
    );

//    @SubscribeEvent
    public void checkWaveCompletion(ServerLevel level, ServerPlayer player) {
        if (!running) return;
        if (tracker.allMobsDead())
        {
            scheduleNextWave(level, player);
            LOGGER.info("Wave completed moving on to next wave");
        }
    }


    private void scheduleNextWave(ServerLevel level, ServerPlayer player) {
        LOGGER.info("[WaveManager] Scheduling next wave in 30 seconds...");
        runDelayed(level, 30 * 20, () -> {
            LOGGER.info("[WaveManager] Countdown finished. Starting next wave.");
            waveNumber++;
            runWaveLoop(level, player);
        });
    }

    private void runDelayed(ServerLevel level, int ticks, Runnable task) {
        tickDelay(level, ticks, task);
    }

    private void tickDelay(ServerLevel level, int ticks, Runnable task) {
        if (ticks <= 0) {
            task.run();
            return;
        }

        level.getServer().execute(() -> tickDelay(level, ticks - 1, task));
    }

//    private void countdown(ServerLevel level, ServerPlayer player, int seconds) {
//        if (seconds <= 0) {
//            LOGGER.info("[WaveManager] Countdown finished. Starting next wave.");
//            waveNumber++;
//            runWaveLoop(level, player);
//            return;
//        }
//
//        LOGGER.info("[WaveManager] Countdown: {} seconds remaining", seconds);
//        if (seconds % 20 == 0)
//        {
//            player.sendSystemMessage(Component.literal("Next wave in " + seconds + "..."));
//        }
//        level.getServer().execute(() -> countdown(level, player, seconds - 1));
//    }

    private void spawnWave(ServerLevel level, ServerPlayer player, Map<EntityType<?>, Integer> spawns) {

        LOGGER.info("[WaveManager] Spawning wave {}...", waveNumber);

        Vec3 look = player.getLookAngle();

        int targetX = player.blockPosition().getX() + (int)(look.x * 20);
        int targetZ = player.blockPosition().getZ() + (int)(look.z * 20);
        int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetX, targetZ);

        BlockPos spawnPos = new BlockPos(targetX, groundY, targetZ);
        LOGGER.info("[WaveManager] Spawn center: {}", spawnPos);

        spawns.forEach((mobType, count) -> {
            LOGGER.info("[WaveManager] Spawning {} x {}", count, mobType.toShortString());

            for (int i = 0; i < count; i++) {

                BlockPos offsetPos = spawnPos.offset(
                        level.random.nextInt(5) - 2,
                        0,
                        level.random.nextInt(5) - 2
                );

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
                    LOGGER.info("[WaveManager] Spawned {} at {}", mob.getType().toShortString(), offsetPos);
                    tracker.registerMob((LivingEntity) mob);
                } else {
                    LOGGER.warn("[WaveManager] FAILED to spawn {}", mobType.toShortString());
                }
            }
        });

        LOGGER.info("[WaveManager] Wave {} spawn complete. Total mobs: {}", waveNumber, tracker.getRemainingCount());
    }



}