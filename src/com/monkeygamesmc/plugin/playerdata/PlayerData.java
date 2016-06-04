package com.monkeygamesmc.plugin.playerdata;

import java.util.HashMap;

public class PlayerData {

	HashMap<String, String> data;

	// the only real reason for creating a PlayerData object is on the initial
	// player join

	public PlayerData() {

		data = new HashMap<String, String>();

	}

	public PlayerData(HashMap<String, String> data) {

		this.data = data;

	}

	public String getData(String key) {

		return data.get(key);

	}

	// for booleans
	public boolean hasData(String key) {

		return data.containsKey(key);

	}

	public HashMap<String, String> getRawData() {

		return data;

	}

	/*
	 * no modifier so the main plugin can change the data but other plugins have
	 * to go through main plugin to make changes to the data
	 */

	void setData(String key, String value) {

		data.put(key, value);

	}

	void unsetData(String key) {

		data.remove(key);

	}

}
