package net.TimeIsWhat.AIFinal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.raid.RaidEvent;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class RaidEventListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRaidWaveSpawn(RaidEvent.WaveSpawn event) {
        // Cancel vanilla spawns
        event.setCanceled(true);

        ServerLevel level = (ServerLevel) event.getRaid().getLevel();
        BlockPos center = event.getRaid().getCenter();

        ServerPlayer player = level.getNearestPlayer(center.getX(), center.getY(), center.getZ(), 64, false);

        if (player != null) {
            LOGGER.info("Intercepted raid wave spawn at {}. Using custom RaidAiHandler for player {}",
                    center, player.getName().getString());

            RaidAiHandler handler = new RaidAiHandler();
            handler.spawnWave(level, center, player);
        } else {
            LOGGER.warn("Raid wave intercepted but no nearby player found. Skipping custom spawn.");
        }
    }
}