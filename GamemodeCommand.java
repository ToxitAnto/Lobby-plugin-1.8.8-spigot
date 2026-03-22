package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {

    private final LumaPvPLobby plugin;
    private BukkitTask updateTask;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();

    public ScoreboardManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
        startUpdater();
    }

    private void startUpdater() {
        int interval = plugin.getConfig().getInt("scoreboard.update-interval", 20);
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                updateScoreboard(p);
            }
        }, 0L, interval);
    }

    public void reload() {
        if (updateTask != null) updateTask.cancel();
        boards.clear();
        startUpdater();
    }

    public void updateScoreboard(Player player) {
        if (!plugin.getConfig().getBoolean("scoreboard.enabled", true)) return;

        org.bukkit.scoreboard.ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard board = sm.getNewScoreboard();

        String title = colorize(plugin.getConfig().getString("scoreboard.title", "&5&lLumaPvP"));
        Objective obj = board.registerNewObjective("lumapvp", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(title);

        List<String> lines = plugin.getConfig().getStringList("scoreboard.lines");
        int score = lines.size();

        Set<String> usedEntries = new HashSet<>();
        for (String line : lines) {
            String formatted = colorize(replace(line, player));
            while (usedEntries.contains(formatted)) {
                formatted = formatted + "§r";
            }
            usedEntries.add(formatted);
            obj.getScore(formatted).setScore(score--);
        }

        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
    }

    public void removeScoreboard(Player player) {
        boards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private String replace(String line, Player player) {
        int online = plugin.getProxyManager().getOnlinePlayers();
        String rank = plugin.getConfigManager().getRank(player);
        return line
                .replace("{player}", player.getName())
                .replace("{rank}", colorize(rank))
                .replace("{online}", String.valueOf(online > 0 ? online : Bukkit.getOnlinePlayers().size()))
                .replace("{server}", "LumaPvP");
    }

    private String colorize(String s) {
        return s.replace("&", "§");
    }
}
