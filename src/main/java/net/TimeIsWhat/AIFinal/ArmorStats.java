package net.TimeIsWhat.AIFinal;

public class ArmorStats {
    private final String name;
    private final int defense;
    private final float toughness;

    /// Constructor
    public ArmorStats(String name, int defense, float toughness) {
        this.name = name;
        this.defense = defense;
        this.toughness = toughness;
    }

    /// Getters
    public String getName() { return name; }
    public int getDefense() { return defense; }
    public float getToughness() { return toughness; }
    /// Combined score
    public int getArmorScore() { return defense + (int) toughness; }
    /// keep outputs the same
    @Override
    public String toString() {
        return name + " [Defense=" + defense + " , Toughness=" + toughness + "]";

     }
}
