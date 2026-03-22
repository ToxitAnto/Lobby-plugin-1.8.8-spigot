package it.lumapvp.lobby.listeners;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final LumaPvPLobby plugin;

    public PlayerMoveListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        if (event.getTo().getY() < -10) {
            Location lobby = plugin.getConfigManager().getLobbyLocation();
            if (lobby != null) {
                event.getPlayer().teleport(lobby);
                event.getPlayer().sendMessage("\u00a75[LumaPvP] \u00a7fTeleported back to lobby!");
            }
        }
    }
}
