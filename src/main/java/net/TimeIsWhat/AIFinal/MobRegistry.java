package net.TimeIsWhat.AIFinal;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class MobRegistry
{
    public static final Map<EntityType<?>, MobStats> MOB_STATS = new HashMap<>();

    static {
        MOB_STATS.put(EntityType.PILLAGER, new MobStats(7.0, 24.0, "ranged"));
        MOB_STATS.put(EntityType.VINDICATOR, new MobStats(19.5, 24.0, "melee"));
        MOB_STATS.put(EntityType.EVOKER, new MobStats(22.5, 24.0, "caster"));
        MOB_STATS.put(EntityType.RAVAGER, new MobStats(18.0, 100.0, "tank"));
        MOB_STATS.put(EntityType.WITCH, new MobStats(8.0,26.0, "support"));
    }

    public static MobStats getStats(EntityType<?> type) {
        return MOB_STATS.getOrDefault(type, new MobStats(0.0, 0.0, "unknown"));
    }

    /// 1 vindicator = 4 pillagers = 2 evokers
}
