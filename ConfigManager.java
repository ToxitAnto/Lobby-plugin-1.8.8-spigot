package it.lumapvp.lobby.listeners;
import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
public class PlayerDeathListener implements Listener {
    private final LumaPvPLobby plugin;
    public PlayerDeathListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!plugin.getConfig().getBoolean("no-death.enabled", true)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
    }
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        event.setFoodLevel(20);
        event.setCancelled(true);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!plugin.getConfig().getBoolean("no-death.teleport-to-lobby", true)) return;
        event.setRespawnLocation(plugin.getConfigManager().getLobbyLocation());
    }
}
