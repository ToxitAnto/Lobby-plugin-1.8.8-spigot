package it.lumapvp.lobby.listeners;

import it.lumapvp.lobby.LumaPvPLobby;
import it.lumapvp.lobby.managers.InventoryManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private final LumaPvPLobby plugin;
    private final InventoryManager invManager;

    public PlayerJoinQuitListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
        this.invManager = new InventoryManager(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerDataManager().handleFirstJoin(player);
        plugin.getPlayerDataManager().startSession(player);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            Location lobby = plugin.getConfigManager().getLobbyLocation();
            if (lobby != null) player.teleport(lobby);
        }, 2L);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            invManager.giveHotbarItems(player);
        }, 3L);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            plugin.getBoardManager().updateScoreboard(player);
            plugin.getTabListManager().updateTab(player);
        }, 5L);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            plugin.getProxyManager().requestOnlinePlayers(player);
            plugin.getVisibilityManager().onPlayerJoin(player);
        }, 10L);

        String rank = plugin.getConfigManager().getRank(player);
        String format = plugin.getConfig().getString("join-message.format", "&a[+] {rank} &f{player}");
        String msg = ChatColor.translateAlternateColorCodes('&',
                format.replace("{rank}", rank).replace("{player}", player.getName()));
        event.setJoinMessage(plugin.getConfig().getBoolean("join-message.enabled", true) ? msg : null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().stopSession(player);
        plugin.getBoardManager().removeScoreboard(player);
        plugin.getVisibilityManager().onPlayerQuit(player);

        String rank = plugin.getConfigManager().getRank(player);
        String format = plugin.getConfig().getString("quit-message.format", "&c[-] {rank} &f{player}");
        String msg = ChatColor.translateAlternateColorCodes('&',
                format.replace("{rank}", rank).replace("{player}", player.getName()));
        event.setQuitMessage(plugin.getConfig().getBoolean("quit-message.enabled", true) ? msg : null);
    }
}
