package net.TimeIsWhat.AIFinal.waves;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashSet;
import java.util.Set;

public class WaveTracker {

    private static final Set<LivingEntity> activeMobs = new HashSet<>();
    private int deaths = 0;
    private double damageTaken = 0;

    public void reset() {
        activeMobs.clear();
        deaths = 0;
        damageTaken = 0;
    }

    public static void registerMob(LivingEntity mob) {
        activeMobs.add(mob);
    }

    public void onMobDeath(LivingDeathEvent event) {
        activeMobs.remove(event.getEntity());
    }

    public void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        deaths++;
    }

    public void onPlayerDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            damageTaken += event.getAmount();
        }
    }

    public boolean allMobsDead() {
        return activeMobs.isEmpty();
    }

    public int getRemainingCount() {return activeMobs.size();}

    public int getDeaths() { return deaths; }
    public double getDamageTaken() { return damageTaken; }
}
