package net.TimeIsWhat.AIFinal;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class RaidRuleEngine
{
    /// map that contains the relation they all have to each other
    /// 1 vindicator = 4 pillagers = 2 evokers
    private static final Map<EntityType<?>, Double> mobSpawnFactors = new HashMap<>();
    static {
        mobSpawnFactors.put(EntityType.PILLAGER, 5.0);
        mobSpawnFactors.put(EntityType.VINDICATOR, 20.0);
        mobSpawnFactors.put(EntityType.EVOKER, 10.0);
        mobSpawnFactors.put(EntityType.WITCH, 8.0);
        mobSpawnFactors.put(EntityType.RAVAGER, 30.0);
    }

    private static int calculateSpawnCount(EntityType<?> mobType, double playerScore) {
        double factor = mobSpawnFactors.getOrDefault(mobType, 50.0); // default factor
        return (int) Math.max(1, playerScore / factor);
    }
    public static Map<EntityType<?>, Integer> calculateWaveSpawns(double playerScore) {
        Map<EntityType<?>, Integer> result = new HashMap<>();
        for (EntityType<?> mobType : mobSpawnFactors.keySet()) {
            int count = calculateSpawnCount(mobType, playerScore);
            result.put(mobType, count);
        }
        return result;
    }

}
