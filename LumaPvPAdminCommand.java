package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerVisibilityManager {

    private final LumaPvPLobby plugin;
    private final Set<UUID> hiddenPlayers = new HashSet<>();

    public PlayerVisibilityManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    public void toggleVisibility(Player player) {
        if (hiddenPlayers.contains(player.getUniqueId())) {
            showPlayers(player);
        } else {
            hidePlayers(player);
        }
    }

    public boolean isHiding(Player player) {
        return hiddenPlayers.contains(player.getUniqueId());
    }

    public void hidePlayers(Player player) {
        hiddenPlayers.add(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.hidePlayer(other);
            }
        }
    }

    public void showPlayers(Player player) {
        hiddenPlayers.remove(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.showPlayer(other);
            }
        }
    }

    public void onPlayerJoin(Player joined) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (hiddenPlayers.contains(other.getUniqueId())) {
                other.hidePlayer(joined);
            }
        }
    }

    public void onPlayerQuit(Player quit) {
        hiddenPlayers.remove(quit.getUniqueId());
    }
}
