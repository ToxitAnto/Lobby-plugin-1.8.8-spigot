package it.lumapvp.lobby.managers;
import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class PlayerDataManager {
    private final LumaPvPLobby plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Long> sessionStart = new HashMap<>();
    public PlayerDataManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
        setupFile();
    }
    private void setupFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Cannot create playerdata.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    public void save() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Cannot save playerdata.yml: " + e.getMessage());
        }
    }
    public void handleFirstJoin(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!dataConfig.contains(uuid + ".first-join")) {
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            dataConfig.set(uuid + ".first-join", date);
            dataConfig.set(uuid + ".name", player.getName());
            save();
        }
        dataConfig.set(uuid + ".name", player.getName());
        save();
    }
    public String getFirstJoin(Player player) {
        String val = dataConfig.getString(player.getUniqueId() + ".first-join");
        return val != null ? val : "N/A";
    }
    public void startSession(Player player) {
        sessionStart.put(player.getUniqueId(), System.currentTimeMillis());
    }
    public void stopSession(Player player) {
        UUID uuid = player.getUniqueId();
        if (!sessionStart.containsKey(uuid)) return;
        long elapsed = System.currentTimeMillis() - sessionStart.get(uuid);
        long stored = dataConfig.getLong(uuid + ".playtime-ms", 0L);
        dataConfig.set(uuid + ".playtime-ms", stored + elapsed);
        sessionStart.remove(uuid);
        save();
    }
    public String getFormattedPlaytime(Player player) {
        UUID uuid = player.getUniqueId();
        long total = dataConfig.getLong(uuid + ".playtime-ms", 0L);
        if (sessionStart.containsKey(uuid)) {
            total += System.currentTimeMillis() - sessionStart.get(uuid);
        }
        long seconds = total / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) return days + "g " + (hours % 24) + "h " + (minutes % 60) + "m";
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }
    public int getStat(Player player, String key) {
        return dataConfig.getInt(player.getUniqueId() + ".stats." + key, 0);
    }
    public void setStat(Player player, String key, int value) {
        dataConfig.set(player.getUniqueId() + ".stats." + key, value);
        save();
    }
    public void incrementStat(Player player, String key) {
        setStat(player, key, getStat(player, key) + 1);
    }
}
