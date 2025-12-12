package net.TimeIsWhat.AIFinal.waves;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

import static com.mojang.text2speech.Narrator.LOGGER;

public class WaveSpawn
{
    private double difficulty = 0.0;
    private double playerScore = 0.0;
    private int playerDeaths = 0;
    private int takenDamage  = 0;

    public WaveSpawn(double difficulty, double playerScore, int playerDeaths, int takenDamage)
    {
        this.difficulty = difficulty;
        this.playerDeaths = playerDeaths;
        this.playerScore = playerScore;
        this.takenDamage = takenDamage;

        LOGGER.info("[WaveSpawn] Created with difficulty={}, score={}, deaths={}, damage={}",
                difficulty, playerScore, playerDeaths, takenDamage);
    }

    public double adjustDifficulty(double difficulty, int playerDeaths, double takenDamage)
    {
        double deathDelta = adjustForDeaths(playerDeaths);
        double damageDelta = adjustForDamge(takenDamage);
        double delta = deathDelta + damageDelta;

        LOGGER.info("[WaveSpawn] Difficulty adjustment: base={} deathsDelta={} damageDelta={} totalDelta={}",
                difficulty, deathDelta, damageDelta, delta);

        double newDifficulty = Math.max(.1, (difficulty + delta));

        LOGGER.info("[WaveSpawn] New difficulty={}", newDifficulty);

        return newDifficulty;
    }

    public double adjustForDeaths(int playerDeaths)
    {
        if (playerDeaths <= 1) {
            LOGGER.info("[WaveSpawn] Death adjustment: {} deaths → no penalty", playerDeaths);
            return 0.0;
        }

        double penalty = -.2 * (playerDeaths - 1);

        LOGGER.info("[WaveSpawn] Death adjustment: {} deaths → penalty {}", playerDeaths, penalty);

        return penalty;
    }

    public double adjustForDamge(double takenDamage)
    {
        double ratio = takenDamage / 20.0;
        double delta = (0.5 - ratio) * 0.3;

        LOGGER.info("[WaveSpawn] Damage adjustment: damage={} ratio={} delta={}",
                takenDamage, ratio, delta);

        return delta;
    }

    // Mob weights
    private static final Map<EntityType<?>, Double> mobSpawnFactors = new HashMap<>();
    static {
        mobSpawnFactors.put(EntityType.PILLAGER, 5.0);
        mobSpawnFactors.put(EntityType.VINDICATOR, 20.0);
        mobSpawnFactors.put(EntityType.EVOKER, 10.0);
        mobSpawnFactors.put(EntityType.WITCH, 8.0);
        mobSpawnFactors.put(EntityType.RAVAGER, 30.0);
    }

    public static Map<EntityType<?>, Integer> calculateWave(double difficulty, double playerScore) {

        LOGGER.info("[WaveSpawn] Calculating wave: difficulty={} playerScore={}", difficulty, playerScore);

        Map<EntityType<?>, Integer> result = new HashMap<>();

        double budget = difficulty * playerScore;

        LOGGER.info("[WaveSpawn] Initial wave budget={}", budget);

        for (Map.Entry<EntityType<?>, Double> entry : mobSpawnFactors.entrySet()) {
            EntityType<?> type = entry.getKey();
            double weight = entry.getValue();

            int count = (int) Math.floor(budget / weight);

            LOGGER.info("[WaveSpawn] Mob={} weight={} → count={} (budget before={})",
                    type.toShortString(), weight, count, budget);

            if (count > 0) {
                result.put(type, count);
                budget -= count * weight;

                LOGGER.info("[WaveSpawn] Mob={} consumed {} budget. Remaining={}",
                        type.toShortString(), (count * weight), budget);
            } else {
                result.put(type, 0);
                LOGGER.info("[WaveSpawn] Mob={} gets 0 spawns (insufficient budget)", type.toShortString());
            }
        }

        LOGGER.info("[WaveSpawn] Final wave composition: {}", result);

        return result;
    }
}