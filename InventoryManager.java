package it.lumapvp.lobby.listeners;
import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
public class ChatListener implements Listener {
    private final LumaPvPLobby plugin;
    public ChatListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        String rank = plugin.getConfigManager().getRank(event.getPlayer());
        String coloredRank = ChatColor.translateAlternateColorCodes('&', rank);
        event.setFormat(coloredRank + " §f" + event.getPlayer().getName() + " §8» §f%2$s");
    }
}
