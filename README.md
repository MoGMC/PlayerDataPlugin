# PlayerDataPlugin
stores player data in easy to access format

###### Accessing Player Data
To access player data, first get the plugin from the Bukkit API.

It might be helpful to get and store it during `onEnable()`.


```java
PlayerDataPlugin myDatabase = Bukkit.getServer().getServicesManager().load(PlayerDataPlugin.class);
```

The plugin's API functions can then be called from the object.

```java
PlayerData myPlayerData = myDatabase.getPlayerData(playerUuid);
String myData = myPlayerData.getString("my_key");
```


Please note that `getPlayerData()` will only get online player's data while `getOfflinePlayerData()` fetches the data from storage.

Please use `getOfflinePlayerData()` _only_ in cases where you need an offline player's data, as it has to search the entire storage for the player. (v.s. `getPlayerData()`, which is limited to online players)
