package net.TimeIsWhat.AIFinal;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import org.slf4j.Logger;

import javax.swing.*;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "aifinal")

public class PlayerEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /// creating a list of raid mobs
    private static final Set<Class<?>> RAID_MOBS = Set.of(
            Pillager.class,
            Vindicator.class,
            Evoker.class,
            Ravager.class,
            Witch.class
    );


    private static final Map<UUID, PlayerStats> statsMap = new HashMap<>();

    @SubscribeEvent
    /// logic for when a player dies
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ///  get or create the stats object for this player
            PlayerStats stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
            ///  updating the death counter
            stats.setDeaths(stats.get_deaths() + 1);
            /// output of new death counter
            LOGGER.info("Death count is now {}", stats.get_deaths());
        }
    }

    @SubscribeEvent
    /// logic for when a player kills a mob
    public static void onMobKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            Entity entity = event.getEntity();
            ;
            if (RAID_MOBS.contains(entity.getClass())) {
                PlayerStats stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
                stats.setMobsKilled(stats.get_mobsKilled() + 1);
                LOGGER.info("You have killed {} number of raid mobs", stats.get_mobsKilled());
            }
        }
    }

    @SubscribeEvent
    /// detecting if a close call happened
    public static void playerCloseCall(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            float newHealth = player.getHealth() - event.getAmount();
            if (newHealth > 0 && newHealth <= 6.0f) {
                PlayerStats stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
                stats.setNumberOfCloseCalls(stats.get_numberOfCloseCalls() + 1);
                LOGGER.info("That was your {} close call!", stats.get_numberOfCloseCalls());
            }
        }
    }
    /// armor related data
    @SubscribeEvent
    public static void onArmorChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {/// sorting for only armor
            if (event.getSlot().isArmor()) {
                /// getting the new and old armor for armor score calculation
                ItemStack oldArmor = event.getFrom();
                ItemStack newArmor = event.getTo();

                PlayerStats stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());

                /// checking if armor was removed
                if (!oldArmor.isEmpty() && newArmor.isEmpty()) {
                    ArmorStats oldStats = ArmorRegistry.getStats(oldArmor.getItem());
                    stats.setArmorScore(stats.get_armorScore() - oldStats.getArmorScore());
                    LOGGER.info("Removed armor. New armor score is: {}", stats.get_armorScore());
                }
                /// checking if armor was only added
                if (oldArmor.isEmpty() && !newArmor.isEmpty()) {
                    ArmorStats newStats = ArmorRegistry.getStats(newArmor.getItem());
                    stats.setArmorScore(stats.get_armorScore() + newStats.getArmorScore());
                    LOGGER.info("Equipped armor. New armor score is: {}", stats.get_armorScore());
                }
                ///  checking for swapped armor
                if (!oldArmor.isEmpty() && !newArmor.isEmpty()) {
                    ArmorStats oldStats = ArmorRegistry.getStats(oldArmor.getItem());
                    ArmorStats newStats = ArmorRegistry.getStats(newArmor.getItem());
                    stats.setArmorScore(stats.get_armorScore() - oldStats.getArmorScore() + newStats.getArmorScore());
                    LOGGER.info("Swapped armor. New armor score is: {}", stats.get_armorScore());
                }
            }
            if (event.getSlot() == EquipmentSlot.OFFHAND)
            {
                ItemStack oldItem = event.getFrom();
                ItemStack newItem = event.getTo();

                PlayerStats stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());

                if (oldItem.getItem() == Items.SHIELD)
                {
                    stats.setWeaponScore(stats.get_weaponScore() - 8);
                    LOGGER.info("Removed shield. New armor score is: {}", stats.get_armorScore());
                }
                if (newItem.getItem() == Items.SHIELD)
                {
                    stats.setWeaponScore(stats.get_weaponScore() + 8);
                    LOGGER.info("Equipped shield. New armor score is: {}", stats.get_armorScore());
                }
            }
        }
    }

    /// for weapon related info
    /// first start up
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerStats p_stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
            Inventory inv = player.getInventory();

            // Reset best-type scores and total before scanning (optional: clear per-type map)
            p_stats.clearBestTypeScores(); // implement this to clear the map if your PlayerStats supports it
            p_stats.setWeaponScore(0);

            // Scan inventory for best weapons by type
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.isEmpty()) continue;

                WeaponStats wstats = WeaponRegistry.WEAPON_STATS.get(stack.getItem());
                if (wstats != null) {
                    String type = wstats.get_type();
                    double strength = wstats.get_weapon_strength();

                    double bestScore = p_stats.get_bestTypeScore(type);
                    if (strength > bestScore) {
                        p_stats.setBestTypeScore(type, strength);
                        p_stats.setWeaponScore(p_stats.get_weaponScore() + strength - bestScore);
                    }
                }
            }
            /// Compute total from best-per-type scores and store it
            double totalScore = p_stats.get_weaponScore(); /// implement this to sum your best-type map
            p_stats.setWeaponScore(totalScore);

            LOGGER.info("Starting weapon score: {}", totalScore);

        }
    }
    /// pick up weapon
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getItem().getItem();

            /// checking if it's a weapon
            WeaponStats newWeapon = WeaponRegistry.WEAPON_STATS.get(stack.getItem());
            if (newWeapon == null) return;


            PlayerStats p_stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());

            double newScore = newWeapon.get_weapon_strength();
            double bestScore = findBestWeaponOfType(player, newWeapon.get_type());
            /// Only update if stronger
            if (newScore > bestScore)
            {
                p_stats.setBestTypeScore((newWeapon.get_type()), newScore);
                double totalScore = p_stats.add_WeaponScores();
                p_stats.setWeaponScore(totalScore);
                LOGGER.info("New stronger weapon. New weapon strength: {}", p_stats.get_weaponScore());

            }
        }
    }

    /// drop weapon
    /// item drop with q or other
    @SubscribeEvent
    public static void onItemDrop(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            ItemStack dropped = event.getEntity().getItem();

            WeaponStats stats = WeaponRegistry.WEAPON_STATS.get(dropped.getItem());
            if (stats == null) return;

            PlayerStats p_stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());

            double bestScore = p_stats.get_bestTypeScore(stats.get_type());

            // If the dropped item was their best weapon, rescan inventory
            if (bestScore == stats.get_weapon_strength()) {
                double newBest = findBestWeaponOfType(player, stats.get_type());
                p_stats.setBestTypeScore(stats.get_type(), newBest);
                p_stats.setWeaponScore(p_stats.get_weaponScore() - bestScore);

                LOGGER.info("Removed strongest weapon. New weapon strength: {}", p_stats.get_weaponScore());
            }
        }
    }
    /// put in container or take out of container
    @SubscribeEvent
    public static void onItemMovedToContainer(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerStats p_stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());
            Inventory inv = player.getInventory();

            /// reset the best scores to find the new best
            p_stats.clearBestTypeScores();
            p_stats.setWeaponScore(0);

            /// scanning the inventory
            for (int i = 0; i < inv.getContainerSize(); i++)
            {
                ItemStack stack = inv.getItem(i);
                if (stack.isEmpty()) continue;

                WeaponStats stats = WeaponRegistry.WEAPON_STATS.get(stack.getItem());
                if (stats != null)
                {
                    String type = stats.get_type();
                    double strength = stats.get_weapon_strength();
                    double bestscore = p_stats.get_bestTypeScore(type);
                    /// sets new best if needed
                    if (strength > bestscore) { p_stats.setBestTypeScore(type, strength); }
                }
            }

            double toatalScore = p_stats.add_WeaponScores();
            p_stats.setWeaponScore(toatalScore);

            LOGGER.info("Used container. The weapon strength: {}", p_stats.get_weaponScore());

        }
    }

    /// helper function
    /// returns the best weapon in an inventory
    public static double findBestWeaponOfType(Player player, String type)
    {
        double best = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i ++)
        {
            ItemStack stack = inv.getItem(i);
            WeaponStats stats = WeaponRegistry.WEAPON_STATS.get(stack.getItem());
            if (stats != null && stats.get_type().equals(type))
            {
                best = Math.max(best, stats.get_weapon_strength());
            }
        }
        return best;
    }

    public static Map<UUID, PlayerStats> getStatsMap() {
        return statsMap;
    }
    /// automaticallty gives the player score so don't need to add a lot of extra steps
    public static double getPlayerScore(ServerPlayer player) {
        PlayerStats stats = statsMap.get(player.getUUID());
        if (stats != null) {
            return stats.get_playerScore(); // assuming PlayerStats has this method
        }
        return 0.0; // default if no stats found
    }


}
