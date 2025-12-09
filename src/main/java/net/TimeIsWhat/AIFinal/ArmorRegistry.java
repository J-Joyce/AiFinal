package net.TimeIsWhat.AIFinal;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;

public class ArmorRegistry {
    // Unified structure: Map<Item, ArmorStats>
    public static final Map<Item, ArmorStats> ARMOR_STATS = new HashMap<>();

    static {
        // Leather
        ARMOR_STATS.put(Items.LEATHER_HELMET, new ArmorStats("Leather Helmet", 1, 0));
        ARMOR_STATS.put(Items.LEATHER_CHESTPLATE, new ArmorStats("Leather Chestplate", 3, 0));
        ARMOR_STATS.put(Items.LEATHER_LEGGINGS, new ArmorStats("Leather Leggings", 2, 0));
        ARMOR_STATS.put(Items.LEATHER_BOOTS, new ArmorStats("Leather Boots", 1, 0));

        // Gold
        ARMOR_STATS.put(Items.GOLDEN_HELMET, new ArmorStats("Gold Helmet", 2, 0));
        ARMOR_STATS.put(Items.GOLDEN_CHESTPLATE, new ArmorStats("Gold Chestplate", 5, 0));
        ARMOR_STATS.put(Items.GOLDEN_LEGGINGS, new ArmorStats("Gold Leggings", 3, 0));
        ARMOR_STATS.put(Items.GOLDEN_BOOTS, new ArmorStats("Gold Boots", 1, 0));

        // Chainmail
        ARMOR_STATS.put(Items.CHAINMAIL_HELMET, new ArmorStats("Chainmail Helmet", 2, 0));
        ARMOR_STATS.put(Items.CHAINMAIL_CHESTPLATE, new ArmorStats("Chainmail Chestplate", 5, 0));
        ARMOR_STATS.put(Items.CHAINMAIL_LEGGINGS, new ArmorStats("Chainmail Leggings", 4, 0));
        ARMOR_STATS.put(Items.CHAINMAIL_BOOTS, new ArmorStats("Chainmail Boots", 1, 0));

        // Copper
        ARMOR_STATS.put(Items.COPPER_HELMET, new ArmorStats("Copper Helmet", 2, 0));
        ARMOR_STATS.put(Items.COPPER_CHESTPLATE, new ArmorStats("Copper Chestplate", 4, 0));
        ARMOR_STATS.put(Items.COPPER_LEGGINGS, new ArmorStats("Copper Leggings", 3, 0));
        ARMOR_STATS.put(Items.COPPER_BOOTS, new ArmorStats("Copper Boots", 1, 0));

        // Iron
        ARMOR_STATS.put(Items.IRON_HELMET, new ArmorStats("Iron Helmet", 2, 0));
        ARMOR_STATS.put(Items.IRON_CHESTPLATE, new ArmorStats("Iron Chestplate", 6, 0));
        ARMOR_STATS.put(Items.IRON_LEGGINGS, new ArmorStats("Iron Leggings", 5, 0));
        ARMOR_STATS.put(Items.IRON_BOOTS, new ArmorStats("Iron Boots", 2, 0));

        // Diamond
        ARMOR_STATS.put(Items.DIAMOND_HELMET, new ArmorStats("Diamond Helmet", 3, 2));
        ARMOR_STATS.put(Items.DIAMOND_CHESTPLATE, new ArmorStats("Diamond Chestplate", 8, 2));
        ARMOR_STATS.put(Items.DIAMOND_LEGGINGS, new ArmorStats("Diamond Leggings", 6, 2));
        ARMOR_STATS.put(Items.DIAMOND_BOOTS, new ArmorStats("Diamond Boots", 3, 2));

        // Netherite
        ARMOR_STATS.put(Items.NETHERITE_HELMET, new ArmorStats("Netherite Helmet", 3, 3));
        ARMOR_STATS.put(Items.NETHERITE_CHESTPLATE, new ArmorStats("Netherite Chestplate", 8, 3));
        ARMOR_STATS.put(Items.NETHERITE_LEGGINGS, new ArmorStats("Netherite Leggings", 6, 3));
        ARMOR_STATS.put(Items.NETHERITE_BOOTS, new ArmorStats("Netherite Boots", 3, 3));

        // Turtle Shell
        ARMOR_STATS.put(Items.TURTLE_HELMET, new ArmorStats("Turtle Shell", 2, 0));
    }

    // Lookup helper
    public static ArmorStats getStats(Item item) {
        return ARMOR_STATS.get(item);
    }
}