package net.Indyuce.mmoitems.api.interaction;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.weapon.Gauntlet;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Crossbow;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Lute;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Musket;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Staff;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Whip;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;
import net.Indyuce.mmoitems.stat.data.CommandData;
import net.Indyuce.mmoitems.stat.data.CommandListData;
import net.mmogroup.mmolib.api.item.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class UseItem {
	protected final Player player;
	protected final PlayerData playerData;
	protected final VolatileMMOItem mmoitem;

	protected static final Random random = new Random();

	public UseItem(Player player, NBTItem nbtItem) {
		this(PlayerData.get(player), nbtItem);
	}

	public UseItem(PlayerData playerData, NBTItem nbtItem) {
		this.player = playerData.getPlayer();
		this.playerData = playerData;
		this.mmoitem = new VolatileMMOItem(nbtItem);
	}

	public Player getPlayer() {
		return player;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public MMOItem getMMOItem() {
		return mmoitem;
	}

	public NBTItem getNBTItem() {
		return mmoitem.getNBT();
	}

	public ItemStack getItem() {
		return mmoitem.getNBT().getItem();
	}

	public boolean canBeUsed() {
		return playerData.getRPG().canUse(mmoitem.getNBT(), true);
	}

	public void executeCommands() {
		if (MMOItems.plugin.getFlags().isFlagAllowed(player, CustomFlag.MI_COMMANDS) && mmoitem.hasData(ItemStats.COMMANDS))
			((CommandListData) mmoitem.getData(ItemStats.COMMANDS)).getCommands().forEach(this::scheduleCommandExecution);
	}

	private void scheduleCommandExecution(CommandData command) {
		String parsed = MMOItems.plugin.getPlaceholderParser().parse(player, command.getCommand());

		if (!command.hasDelay())
			dispatchCommand(parsed, command.isConsoleCommand(), command.hasOpPerms());
		else
			Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> dispatchCommand(parsed, command.isConsoleCommand(), command.hasOpPerms()),
					(long) command.getDelay() * 20);
	}

	private void dispatchCommand(String parsed, boolean console, boolean op) {
		if (console) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
			return;
		}

		if (op && !player.isOp()) {
			player.setOp(true);
			try {
				Bukkit.dispatchCommand(player, parsed);
			} catch (Exception ignored) {}
			player.setOp(false);
		} else
			Bukkit.dispatchCommand(player, parsed);
	}

	public static UseItem getItem(Player player, NBTItem item, String type) {
		return getItem(player, item, Type.get(type));
	}

	public static UseItem getItem(Player player, NBTItem item, Type type) {
		if (type.corresponds(Type.CONSUMABLE))
			return new Consumable(player, item);
		if (type.corresponds(Type.SKIN))
			return new ItemSkin(player, item);
		if (type.corresponds(Type.GEM_STONE))
			return new GemStone(player, item);
		if (type.corresponds(Type.MUSKET))
			return new Musket(player, item);
		if (type.corresponds(Type.CROSSBOW))
			return new Crossbow(player, item);
		if (type.corresponds(Type.GAUNTLET))
			return new Gauntlet(player, item);
		if (type.corresponds(Type.WHIP))
			return new Whip(player, item);
		if (type.corresponds(Type.LUTE))
			return new Lute(player, item);
		if (type.corresponds(Type.STAFF))
			return new Staff(player, item);
		return type.isWeapon() ? new Weapon(player, item) : new UseItem(player, item);
	}
}
