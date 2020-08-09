package net.Indyuce.mmoitems.api.edition.process;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.Edition;
import net.mmogroup.mmolib.MMOLib;

public class AnvilGUI extends EditionProcess implements Listener {
	private final int containerId;
	private final Inventory inventory;

	private boolean open;

	public AnvilGUI(Edition edition) {
		super(edition);

		ItemStack paper = new ItemStack(Material.PAPER);
		ItemMeta paperMeta = paper.getItemMeta();
		paperMeta.setDisplayName("Input text..");
		paper.setItemMeta(paperMeta);

		MMOLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(getPlayer());
		MMOLib.plugin.getVersion().getWrapper().setActiveContainerDefault(getPlayer());

		Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);

		final Object container = MMOLib.plugin.getVersion().getWrapper().newContainerAnvil(getPlayer());

		inventory = MMOLib.plugin.getVersion().getWrapper().toBukkitInventory(container);
		inventory.setItem(0, paper);

		containerId = MMOLib.plugin.getVersion().getWrapper().getNextContainerId(getPlayer());
		MMOLib.plugin.getVersion().getWrapper().sendPacketOpenWindow(getPlayer(), containerId);
		MMOLib.plugin.getVersion().getWrapper().setActiveContainer(getPlayer(), container);
		MMOLib.plugin.getVersion().getWrapper().setActiveContainerId(container, containerId);
		MMOLib.plugin.getVersion().getWrapper().addActiveContainerSlotListener(container, getPlayer());

		open = true;
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void close() {
		if (!open)
			return;
		open = false;

		MMOLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(getPlayer());
		MMOLib.plugin.getVersion().getWrapper().setActiveContainerDefault(getPlayer());
		MMOLib.plugin.getVersion().getWrapper().sendPacketCloseWindow(getPlayer(), containerId);

		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void a(InventoryClickEvent event) {
		if (event.getInventory().equals(inventory)) {
			event.setCancelled(true);
			if (event.getRawSlot() == 2) {
				ItemStack clicked = inventory.getItem(event.getRawSlot());
				if (clicked != null && clicked.getType() != Material.AIR)
					registerInput(clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());
			}
		}
	}

	@EventHandler
	public void b(InventoryCloseEvent event) {
		if (open && event.getInventory().equals(inventory))
			close();
	}
}