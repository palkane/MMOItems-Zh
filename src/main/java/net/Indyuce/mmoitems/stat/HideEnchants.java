package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.item.MMOItem;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.mmogroup.mmolib.api.item.NBTItem;

public class HideEnchants extends BooleanStat {
	public HideEnchants() {
		super("HIDE_ENCHANTS", new ItemStack(Material.BOOK), "Hide Enchantments", new String[] { "Enable to completely hide your item", "enchants. You can still see the glowing effect." }, new String[] { "all" });
	}

	@Override
	public void whenApplied(MMOItemBuilder item, StatData data) {
		if (((BooleanData) data).isEnabled())
			item.getMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
	}

	@Override
	public void whenLoaded(MMOItem mmoitem, NBTItem item) {
		if (item.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
			mmoitem.setData(ItemStat.HIDE_ENCHANTS, new BooleanData(true));
	}
}
