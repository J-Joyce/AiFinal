package net.TimeIsWhat.AIFinal;

public class MobStats
{
    private final double damage;
    private final double health;
    private final String role;

    MobStats (double damage, double health, String role)
    {
        this.damage = damage;
        this.health = health;
        this.role = role;
    }
    /// calculating combatStrenght
    public double calculating_CombatStrength ()
    {
        return (damage * .2) + (health * .5);
    }
}
