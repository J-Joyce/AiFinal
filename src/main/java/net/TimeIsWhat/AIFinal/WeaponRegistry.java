package net.TimeIsWhat.AIFinal;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.HashMap;
import java.util.Map;

public class WeaponRegistry
{
    public static final Map<Item, WeaponStats> WEAPON_STATS = new HashMap<>();

    static
    {
        /// Swords
        WEAPON_STATS.put(Items.WOODEN_SWORD,    new WeaponStats("Wooden Sword",    4, 1.6f, "sword"));
        WEAPON_STATS.put(Items.GOLDEN_SWORD,    new WeaponStats("Golden Sword",    4, 1.6f, "sword"));
        WEAPON_STATS.put(Items.STONE_SWORD,     new WeaponStats("Stone Sword",     5, 1.6f, "sword"));
        WEAPON_STATS.put(Items.COPPER_SWORD,    new WeaponStats("Copper Sword",    5, 1.6f, "sword"));
        WEAPON_STATS.put(Items.IRON_SWORD,      new WeaponStats("Iron Sword",      6, 1.6f, "sword"));
        WEAPON_STATS.put(Items.DIAMOND_SWORD,   new WeaponStats("Diamond Sword",   7, 1.6f, "sword"));
        WEAPON_STATS.put(Items.NETHERITE_SWORD, new WeaponStats("Netherite Sword", 8, 1.6f, "sword"));

        /// Axes
        WEAPON_STATS.put(Items.WOODEN_AXE,      new WeaponStats("Wooden Axe",      7, 0.8f, "axe"));
        WEAPON_STATS.put(Items.GOLDEN_AXE,      new WeaponStats("Golden Axe",      7, 1.0f, "axe"));
        WEAPON_STATS.put(Items.STONE_AXE,       new WeaponStats("Stone Axe",       9, 0.8f, "axe"));
        WEAPON_STATS.put(Items.COPPER_AXE,      new WeaponStats("Copper Axe",      8, 1.0f, "axe"));
        WEAPON_STATS.put(Items.IRON_AXE,        new WeaponStats("Iron Axe",        9, 0.9f, "axe"));
        WEAPON_STATS.put(Items.DIAMOND_AXE,     new WeaponStats("Diamond Axe",     9, 1.0f, "axe"));
        WEAPON_STATS.put(Items.NETHERITE_AXE,   new WeaponStats("Netherite Axe",  10, 1.0f, "axe"));

        /// Trident
        WEAPON_STATS.put(Items.TRIDENT,         new WeaponStats("Trident",         9, 1.1f, "trident"));

        /// Mace
        WEAPON_STATS.put(Items.MACE,            new WeaponStats("Mace",           10, 0.6f, "mace"));

        /// Bow (full draw only)
        WEAPON_STATS.put(Items.BOW,             new WeaponStats("Bow (full draw)", 9, 1.0f, "ranged"));

        /// Crossbow
        WEAPON_STATS.put(Items.CROSSBOW,        new WeaponStats("Crossbow",        9, 0.8f, "ranged"));
    }

}
