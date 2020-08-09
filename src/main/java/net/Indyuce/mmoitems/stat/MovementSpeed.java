package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.StatFormat;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.AttributeStat;
import net.mmogroup.mmolib.api.item.ItemTag;

public class MovementSpeed extends AttributeStat {
	public MovementSpeed() {
		super("MOVEMENT_SPEED", new ItemStack(Material.LEATHER_BOOTS), "Movement Speed", new String[] { "Movement Speed increase walk speed.", "Default MC walk speed: 0.1" }, Attribute.GENERIC_MOVEMENT_SPEED);
	}

	@Override
	public void whenApplied(MMOItemBuilder item, StatData data) {
		double value = ((DoubleData) data).generateNewValue();
		// for (String slot : item.getMMOItem().getType().getSlots())
		// item.addItemAttribute(new Attribute("movementSpeed", value, slot));
		item.addItemTag(new ItemTag("MMOITEMS_MOVEMENT_SPEED", value));
		item.getLore().insert("movement-speed", formatNumericStat(value, "#", new StatFormat("####").format(value)));
	}
}
