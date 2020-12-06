package net.Indyuce.mmoitems.comp.mmocore.load;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.mmogroup.mmolib.api.MMOLineConfig;
import net.mmogroup.mmolib.api.util.SmartGive;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

public class MMOItemTrigger extends Trigger {
	private final MMOItemTemplate template;
	private final int amount;

	public MMOItemTrigger(MMOLineConfig config) {
		super(config);

		config.validate("type", "id");

		String format = config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
		Validate.isTrue(MMOItems.plugin.getTypes().has(format), "Could not find item type with ID '" + format + "'");
		Type type = MMOItems.plugin.getTypes().get(format);

		String id = config.getString("id").replace("-", "_").toUpperCase();
		Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(type, id), "Could not find MMOItem with ID '" + id + "'");
		template = MMOItems.plugin.getTemplates().getTemplate(type, id);

		amount = config.args().length > 0 ? Math.max(1, Integer.parseInt(config.args()[0])) : 1;
	}

	@Override
	public void apply(PlayerData player) {
		ItemStack item = template.newBuilder(net.Indyuce.mmoitems.api.player.PlayerData.get(player.getUniqueId()).getRPG()).build().newBuilder().build();
		item.setAmount(amount);
		new SmartGive(player.getPlayer()).give(item);
	}
}
