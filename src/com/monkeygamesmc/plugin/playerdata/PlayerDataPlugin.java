package com.monkeygamesmc.plugin.playerdata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDataPlugin extends JavaPlugin implements Listener {

	// TODO: for boolean values: if a value is stored, it is true; if it does
	// not exist, it is false.

	YamlConfiguration data;
	File dataFile;

	HashMap<UUID, PlayerData> playerData;

	// commands for debugging and stuff
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String cmd = command.getName();

		if (args.length < 1) {
			return false;

		}

		@SuppressWarnings("deprecation")
		Player player = Bukkit.getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage("That player could not be found on this server.");
			return true;

		}

		if (cmd.equals("showplayerdata")) {

			StringBuilder info = new StringBuilder(ChatColor.GRAY.toString());

			info.append(player.getName());
			info.append(" (");
			info.append(player.getDisplayName());
			info.append(", ");
			info.append(player.getUniqueId().toString());
			info.append(")\n");

			HashMap<String, String> rawData = getPlayerData(player.getUniqueId()).getRawData();

			for (String key : rawData.keySet()) {

				info.append(key);
				info.append(": ");
				info.append(rawData.get(key));
				info.append("\n");

			}

			info.append("End of data.");

			sender.sendMessage(info.toString());

			return true;

		} else if (cmd.equals("setplayerdata")) {

			if (args.length < 3) {
				return false;

			}

			setData(player.getUniqueId(), args[1], args[2]);

			sender.sendMessage(
					ChatColor.GRAY + "Set `" + args[1] + "` to `" + args[2] + "` for player " + player.getName() + ".");

			return true;

		}

		return false;

	}

	// eventhandlers on highest priority so if the event is cancelled for some
	// reason it won't load or remove the data

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {

		loadPlayerData(e.getPlayer());

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {

		// remove players who leave from data list
		playerData.remove(e.getPlayer().getUniqueId());

	}

	private void loadPlayerData(Player player) {

		System.out.println("AJIOJ");

		// create empty data for players who haven't played before
		if (!player.hasPlayedBefore()) {

			playerData.put(player.getUniqueId(), new PlayerData());
			return;

		}

		// add to list
		playerData.put(player.getUniqueId(), getOfflinePlayerData(player.getUniqueId()));

	}

	/*
	 * all data setting/unsetting is done thru main plugin so the
	 * yamlconfiguration can be updated as it goes (instead of having to save
	 * all the player's data when nothing has changed)
	 */

	public void setData(UUID uuid, String key, String value) {

		data.set(uuid.toString() + "." + key, value);
		playerData.get(uuid).setData(key, value);

	}

	public void unsetData(UUID uuid, String key) {

		data.set(uuid.toString() + "." + key, null);
		playerData.get(uuid).unsetData(key);

	}

	//

	public PlayerData getPlayerData(UUID uuid) {
		return playerData.get(uuid);

	}

	// use sparingly
	public PlayerData getOfflinePlayerData(UUID uuid) {

		// cut out the player's data from file to make fetching easier
		ConfigurationSection section = data.getConfigurationSection(uuid.toString());

		// if the player doesn't have stored data, return empty data.
		if (section == null) {
			return new PlayerData();

		}

		HashMap<String, String> storedData = new HashMap<String, String>();

		// transfer data from file to list
		for (String key : section.getKeys(false)) {
			storedData.put(key, section.getString(key));

		}

		return new PlayerData(storedData);

	}

	@Override
	public void onEnable() {

		// gets data file for reading and writing to
		dataFile = new File(getDataFolder(), "data.yml");

		// checks if it exists & creates it if it doesn't
		if (!dataFile.exists()) {

			try {
				dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();

			} catch (IOException e) {
				// disable plugin if it can't get the data
				Bukkit.getLogger().severe("Could not create new data.yml file.");
				Bukkit.getPluginManager().disablePlugin(this);
				e.printStackTrace();

			}

		}

		// reads from file and stores in YamlConfiguration for editing
		data = YamlConfiguration.loadConfiguration(dataFile);

		playerData = new HashMap<UUID, PlayerData>();

		Bukkit.getPluginManager().registerEvents(this, this);

		Bukkit.getServer().getServicesManager().register(PlayerDataPlugin.class, this, this, ServicePriority.Normal);

	}

	@Override
	public void onDisable() {

		// save data file
		try {
			data.save(dataFile);

		} catch (IOException e) {
			Bukkit.getLogger().severe("Could not save data.yml.");
			e.printStackTrace();

		}

	}

}
