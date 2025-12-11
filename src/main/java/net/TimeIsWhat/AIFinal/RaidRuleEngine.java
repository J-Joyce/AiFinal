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
        double factor = mobSpawnFactors.getOrDefault(mobType, 5.0); // default factor

        if (playerScore <= 14 && factor <= 8)/// copper armor
        {
            return (int) Math.max(0, playerScore / factor);
        }
        else if (playerScore <= 17 && playerScore >= 14.1 && factor <= 10)/// iron armor
        {
            if (mobType == EntityType.WITCH) {return 1;}
            return (int) Math.max(0, playerScore / factor);
        }
        else if (playerScore <= 24 && playerScore >= 17.1 && factor <= 20)/// diamond armor
        {
            if (mobType == EntityType.WITCH) { return (int) Math.min(2, playerScore / factor); }
            if (mobType == EntityType.PILLAGER) { return (int) Math.min(4, playerScore / factor);}
            return (int) Math.max(0, playerScore / factor);
        }
        /// Netheirte armor
        return 0;
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
