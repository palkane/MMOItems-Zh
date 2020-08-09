package net.Indyuce.mmoitems.stat.block;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RequiredPower extends DoubleStat {
    public RequiredPower() {
        super("REQUIRED_POWER", new ItemStack(Material.IRON_PICKAXE), "Required Pickaxe Power", new String[] { "The required pickaxe power", "needed to break this custom block." }, new String[] { "block" });
    }
}
