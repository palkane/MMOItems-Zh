package net.Indyuce.mmoitems.manager;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.PluginUpdate;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.UpdaterData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class PluginUpdateManager {

	/**
	 * The integer as key is used as reference in order to apply an update using
	 * the update command.
	 */
	private final Map<Integer, PluginUpdate> updates = new HashMap<>();

	public PluginUpdateManager() {
		register(new PluginUpdate(1, new String[] { "Applies a fix for skull textures values in 4.7.1.",
				"Texture values data storage changed in 4.7.1 due to the UUID change." }, sender -> {

					for (Type type : MMOItems.plugin.getTypes().getAll()) {
						ConfigFile config = type.getConfigFile();
						for (String key : config.getConfig().getKeys(false)) {
							ConfigurationSection section = config.getConfig().getConfigurationSection(key);
							if (section.contains("skull-texture") && section.get("skull-texture") instanceof String) {
								section.set("skull-texture.value", section.getString("skull-texture"));
								section.set("skull-texture.uuid", UUID.randomUUID().toString());
							}
						}

						config.save();
					}
				}));

		register(new PluginUpdate(3, new String[] { "5.3.2: converts all your crafting station recipes to the newest config format.",
				"&cWarning, running this update will get rid of your # config file comments." }, sender -> {

					for (File file : new File(MMOItems.plugin.getDataFolder() + "/crafting-stations").listFiles()) {
						FileConfiguration config = YamlConfiguration.loadConfiguration(file);

						if (config.contains("recipes")) {
							for (String key : config.getConfigurationSection("recipes").getKeys(false))
								try {

									List<String> ingredients = config.getStringList("recipes." + key + ".ingredients");
									List<String> newest = new ArrayList<String>();

									for (String ingredient : ingredients) {
										String[] split = ingredient.split("\\ ");
										if (split[0].equals("mmoitem")) {
											String format = "mmoitem{type=" + split[1] + ",id=" + split[2];
											if (split.length > 3)
												format += ",amount=" + split[3];
											if (split.length > 4)
												format += ",display=\"" + split[4].replace("_", " ") + "\"";
											newest.add(format + "}");
										}

								else if (split[0].equals("vanilla")) {
											String format = "vanilla{type=" + split[1];
											if (split.length > 2 && !split[2].equals("."))
												format += ",name=\"" + split[2] + "\"";
											if (split.length > 3)
												format += ",amount=" + split[3];
											if (split.length > 4)
												format += ",display=\"" + split[4].replace("_", " ") + "\"";
											newest.add(format + "}");
										}

								else {
											MMOItems.plugin.getLogger().log(Level.INFO, "Config Update 3: Could not match ingredient from '"
													+ ingredient + "' from recipe '" + key + "', added it anyway.");
											newest.add(ingredient);
										}
									}

									config.set("recipes." + key + ".ingredients", newest);

									List<String> conditions = config.getStringList("recipes." + key + ".conditions");
									newest = new ArrayList<>();

									for (String condition : conditions) {
										String[] split = condition.split("\\ ");
										if (split[0].equalsIgnoreCase("class"))
											newest.add("class{list=\"" + condition.replace(split[0] + " ", "").replace(" ", ",") + "\"}");
										else if (split[0].equalsIgnoreCase("perms"))
											newest.add("permission{list=\"" + condition.replace(split[0] + " ", "").replace(" ", ",") + "\"}");
										else if (split[0].equalsIgnoreCase("food") || split[0].equals("mana") || split[0].equals("stamina"))
											newest.add(split[0] + "{amount=" + split[1] + "}");
										else if (split[0].equalsIgnoreCase("level"))
											newest.add("level{level=" + split[1] + "}");
										else if (split[0].equalsIgnoreCase("profession"))
											newest.add("profession{profession=" + split[1] + ",level=" + split[2] + "}");
										else if (split[0].equalsIgnoreCase("exp"))
											newest.add("exp{profession=" + split[1] + ",amount=" + split[2] + "}");
										else {
											MMOItems.plugin.getLogger().log(Level.INFO, "Config Update 3: Could not match condition from '"
													+ condition + "' from recipe '" + key + "', added it anyway.");
											newest.add(condition);
										}
									}

									config.set("recipes." + key + ".conditions", newest);
								} catch (Exception exception) {
									MMOItems.plugin.getLogger().log(Level.INFO,
											"Config Update 3: Could not convert recipe with key '" + key + "': " + exception.getMessage());
								}

							try {
								config.save(file);
							} catch (IOException exception) {
								MMOItems.plugin.getLogger().log(Level.INFO,
										"Config Update 3: Could not save config '" + file.getName() + "': " + exception.getMessage());
							}
						}
					}
				}));

		register(new PluginUpdate(2,
				new String[] { "Enables the item updater for every item.", "&cNot recommended unless you know what you are doing." }, sender -> {
					for (Type type : MMOItems.plugin.getTypes().getAll())
						for (String id : type.getConfigFile().getConfig().getKeys(false))
							MMOItems.plugin.getUpdater().enable(new UpdaterData(type, id, UUID.randomUUID(), true));
				}));

		register(new PluginUpdate(4,
				new String[] { "Transforms all your current MMOItems into item templates and fixes some stat formats which have been changed.",
						"&cIt is REALLY important to save a backup before using this config update!" },
				sender -> {

					// fixes stat formats
					for (Type type : MMOItems.plugin.getTypes().getAll()) {
						ConfigFile config = type.getConfigFile();
						for (String id : config.getConfig().getKeys(false)) {

							// translates items into templates
							if (!config.getConfig().getConfigurationSection(id).contains("base")) {

								config.getConfig().createSection(id + ".base", config.getConfig().getConfigurationSection(id).getValues(false));
								for (String statKey : config.getConfig().getConfigurationSection(id).getKeys(false))
									if (!statKey.equals("base"))
										config.getConfig().set(id + "." + statKey, null);
							}
							// simple path changes
							rename(config.getConfig().getConfigurationSection(id + ".base"), "regeneration", "health-regeneration");
							rename(config.getConfig().getConfigurationSection(id + ".base"), "element.light", "element.lightness");
							rename(config.getConfig().getConfigurationSection(id + ".base"), "consume-cooldown", "item-cooldown");

							// sound changes
							if (config.getConfig().getConfigurationSection(id + ".base").contains("consume-sound")) {
								rename(config.getConfig().getConfigurationSection(id + ".base"), "consume-sound", "sounds.on-consume.sound");
								config.getConfig().set(id + ".base.sounds.on-consume.volume", 1.0D);
								config.getConfig().set(id + ".base.sounds.on-consume.pitch", 1.0D);
							}

							// effects changes
							if (config.getConfig().getConfigurationSection(id + ".base").contains("effects")) {
								for (String effect : config.getConfig().getConfigurationSection(id + ".base.effects").getKeys(false)) {
									String[] split = config.getConfig().getString(id + ".base.effects." + effect).split(",");
									if (split.length > 1) {
										config.getConfig().set(id + ".base.new-effects." + effect + ".duration", Double.parseDouble(split[0]));
										config.getConfig().set(id + ".base.new-effects." + effect + ".amplifier", Double.parseDouble(split[1]));
									}
								}
								config.getConfig().set(id + ".base.effects", null);
								rename(config.getConfig().getConfigurationSection(id + ".base"), "new-effects", "effects");
							}

							if (config.getConfig().getConfigurationSection(id + ".base").contains("restore")) {
								config.getConfig().set(id + ".base.restore-health", config.getConfig().getDouble(id + ".base.restore.health"));
								config.getConfig().set(id + ".base.restore-food", config.getConfig().getDouble(id + ".base.restore.food"));
								config.getConfig().set(id + ".base.restore-saturation",
										config.getConfig().getDouble(id + ".base.restore.saturation"));
								config.getConfig().set(id + ".base.restore", null);
							}

							// fix numeric stat formats
							for (String statKey : config.getConfig().getConfigurationSection(id + ".base").getKeys(false)) {

								String str = config.getConfig().getString(id + ".base." + statKey);
								if (str != null)
									try {

										String[] split = str.split("\\=");
										Validate.isTrue(split.length == 2);
										double val1 = Double.parseDouble(split[0]);
										double val2 = Double.parseDouble(split[1]);

										double avg = (val1 + val2) / 2;
										double max = Math.max(Math.abs(val1), Math.abs(val2));
										double rel = (max - avg) / max;

										config.getConfig().set(id + ".base." + statKey + ".base", avg);
										config.getConfig().set(id + ".base." + statKey + ".spread", rel / 3);
										config.getConfig().set(id + ".base." + statKey + ".max-spread", rel);

									} catch (Exception e) {
									}
							}

						}
						config.save();
					}
				}));
	}

	public void register(PluginUpdate update) {
		updates.put(update.getId(), update);
	}

	public boolean has(int id) {
		return updates.containsKey(id);
	}

	public PluginUpdate get(int id) {
		return updates.get(id);
	}

	public Collection<PluginUpdate> getAll() {
		return updates.values();
	}

	private void rename(ConfigurationSection config, String oldPath, String newPath) {
		if (config.contains(oldPath)) {
			Object temp = config.get(oldPath);
			config.set(oldPath, null);
			config.set(newPath, temp);
		}
	}

}
