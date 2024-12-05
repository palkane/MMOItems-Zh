package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.version.Attributes;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.type.AttackWeaponStat;
import org.bukkit.Material;

@HasCategory(cat = "vanilla_attribute")
public class AttackDamage extends AttackWeaponStat {
    public AttackDamage() {
        super("ATTACK_DAMAGE",
                Material.IRON_SWORD,
                "攻击伤害",
                new String[]{"你的武器造成的伤害量"},
                Attributes.ATTACK_DAMAGE);
    }
}
