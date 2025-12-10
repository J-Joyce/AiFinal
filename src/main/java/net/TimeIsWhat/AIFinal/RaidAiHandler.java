package net.TimeIsWhat.AIFinal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Map;

import net.TimeIsWhat.AIFinal.PlayerEventHandler;

public class RaidAiHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public void spawnWave(ServerLevel level, BlockPos raidCenter, ServerPlayer player) {
        double score = PlayerEventHandler.getPlayerScore(player);
        LOGGER.info("Starting raid wave for player {} with score {}", player.getName().getString(), score);

        Map<EntityType<?>, Integer> spawns = RaidRuleEngine.calculateWaveSpawns(score);
        LOGGER.info("Calculated spawn plan: {}", spawns);

        spawns.forEach((mobType, count) -> {
            LOGGER.info("Spawning {} of type {}", count, mobType.toShortString());

            for (int i = 0; i < count; i++) {
                Entity mob = mobType.spawn(
                        level,
                        null,       // ItemStack (spawn egg) not needed
                        null,       // Custom name
                        raidCenter, // Position
                        EntitySpawnReason.EVENT,
                        true,       // Align to block
                        false       // Spawn on top of block
                );

                if (mob != null) {
                    mob.setPos(
                            raidCenter.getX(),
                            raidCenter.getY(),
                            raidCenter.getZ()
                    );
                    level.addFreshEntity(mob);

                    LOGGER.info("Spawned {} at ({}, {}, {})",
                            mob.getType().toShortString(),
                            raidCenter.getX(),
                            raidCenter.getY(),
                            raidCenter.getZ()
                    );
                } else {
                    LOGGER.warn("Failed to spawn entity of type {}", mobType.toShortString());
                }
            }
        });

        LOGGER.info("Raid wave spawn complete.");
    }
}