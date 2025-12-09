package net.TimeIsWhat.AIFinal;

import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;

public class WeaponStats
{
    private final String name;
    private final float damage;
    private final float attack_speed;
    private final String type;
    /// getters
    public String get_name() { return  name; }
    public float get_damage() { return  damage; }
    public float get_attack_speed() { return attack_speed; }
    public String get_type() {return  type; }

    /// initialization
    public WeaponStats (String name, float damage, float attack_speed, String type)
    {
        this.name = name;
        this.damage = damage;
        this.attack_speed = attack_speed;
        this.type = type;
    }
    /// dps
    public float get_weapon_strength () { return damage * attack_speed; }

    @Override
    public String toString() { return name + " [Damage=" + damage + ", Speed=" + attack_speed + ", Score=" + get_weapon_strength() + "]"; }

}
