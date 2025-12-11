package net.TimeIsWhat.AIFinal;

import java.util.HashMap;
import java.util.Map;

public class PlayerStats {
    /// base stats
    private int deaths;
    private int mobsKilled;
    private int numberOfCloseCalls; /// a close call will be defined as the player dropping to 3 or less hearts
    private long waveClearTime; ///how long it took the player to take out each wave
    private long raidClearTime; ///how long the raid itself took

    /// armor stats & tool stats (enchantments will be modifiers and not directly stored on own but backed into the valuse)
    private int armorScore;
    private double weaponScore;
    private final Map<String, Double> bestTypeScore = new HashMap<>();



    /// getters
    public int get_deaths() {return deaths;}
    public int get_mobsKilled() {return mobsKilled;}
    public int get_numberOfCloseCalls() {return numberOfCloseCalls;}
    public long get_waveClearTime() {return  waveClearTime;}
    public long get_RaidClearTime() {return  raidClearTime;}
    public int get_armorScore() {return  armorScore;}
    public double get_weaponScore() {return  weaponScore;}
    public double get_bestTypeScore(String type) {return bestTypeScore.getOrDefault(type, 0.0); }

    /// setters
    public void setDeaths(int new_deaths) { deaths =new_deaths;}
    public void setMobsKilled(int new_mobsKilled) {mobsKilled = new_mobsKilled;}
    public void setNumberOfCloseCalls(int new_numberOfCloseCalls) {numberOfCloseCalls = new_numberOfCloseCalls;}
    public void setWaveClearTime(long new_time) {waveClearTime = new_time;}
    public void setRaidClearTime(long new_time) {raidClearTime = new_time;}
    public void setArmorScore(int new_armorScore) {armorScore = new_armorScore;}
    public void setWeaponScore(double new_weaponScore) {weaponScore = new_weaponScore;}
    public void setBestTypeScore(String type, double new_score)
    {
        double currentBest = bestTypeScore.getOrDefault(type, 0.0);
        if (new_score > currentBest)
        {
            bestTypeScore.put(type, new_score);
        }
    }

    public void clearBestTypeScores(){
        bestTypeScore.clear();
    }
    /// adding all the different weapons into one score
    public double add_WeaponScores()
    {
        double sum = 0.0;
        for (double v : bestTypeScore.values()) sum += v;
        return sum;
    }

    public double get_playerScore()
    {
        return ((armorScore * .4) + (weaponScore * .6)) * Math.max(0.0, (1 - (.2 * deaths))); ///+ (numberOfCloseCalls);
    }

}

