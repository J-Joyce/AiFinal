package net.TimeIsWhat.AIFinal.waves;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        mobSpawnFactors.put(EntityType.VINDICATOR, 23.0);
        mobSpawnFactors.put(EntityType.EVOKER, 11.0);
        //mobSpawnFactors.put(EntityType.WITCH, 8.0);
        mobSpawnFactors.put(EntityType.RAVAGER, 30.0);
    }

    public static Map<EntityType<?>, Integer> calculateWave(double difficulty, double playerScore) {

        LOGGER.info("[WaveSpawn] Calculating wave: difficulty={} playerScore={}", difficulty, playerScore);

        Map<EntityType<?>, Integer> result = new HashMap<>();

        double budget = difficulty * playerScore;

        LOGGER.info("[WaveSpawn] Initial wave budget={}", budget);

        // Sort mobs by descending weight (most powerful first)
        List<Map.Entry<EntityType<?>, Double>> sorted = new ArrayList<>(mobSpawnFactors.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        LOGGER.info("[WaveSpawn] Sorted mob list (strongest → weakest):");
        for (var e : sorted) {
            LOGGER.info("  Mob={} weight={}", e.getKey().toShortString(), e.getValue());
        }

        //  Greedy allocation
        for (Map.Entry<EntityType<?>, Double> entry : sorted) {
            EntityType<?> type = entry.getKey();
            double weight = entry.getValue();

            LOGGER.info("[WaveSpawn] Evaluating mob={} weight={} with remaining budget={}",
                    type.toShortString(), weight, budget);

            if (budget < weight) {
                LOGGER.info("[WaveSpawn] Mob={} gets 0 spawns (insufficient budget)", type.toShortString());
                result.put(type, 0);
                continue;
            }

            int count = (int) Math.floor(budget / weight);

            LOGGER.info("[WaveSpawn] Mob={} → greedy count={} (budget before={})",
                    type.toShortString(), count, budget);

            result.put(type, count);

            double consumed = count * weight;
            budget -= consumed;

            LOGGER.info("[WaveSpawn] Mob={} consumed {} budget. Remaining={}",
                    type.toShortString(), consumed, budget);

            if (budget <= 0) {
                LOGGER.info("[WaveSpawn] Budget exhausted. Ending greedy allocation.");
                break;
            }
        }

        LOGGER.info("[WaveSpawn] Final wave composition: {}", result);

        return result;
    }
}