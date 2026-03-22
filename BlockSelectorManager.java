package it.lumapvp.lobby.listeners;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class BlocksListener implements Listener {

    private final LumaPvPLobby plugin;

    public BlocksListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.getConfigManager().isInventoryProtectionEnabled()) return;
        if (!plugin.getConfigManager().isNoDrop()) return;
        ItemStack it = event.getItemDrop().getItemStack();
        String name = it.hasItemMeta() && it.getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(it.getItemMeta().getDisplayName()) : "";
        if (isHotbarItem(name)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!plugin.getConfigManager().isInventoryProtectionEnabled()) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        String name = event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()) : "";
        if (isHotbarItem(name)) event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {}

    private boolean isHotbarItem(String name) {
        return name.equals("Selector") || name.equals("Profile") || name.equals("Settings")
                || name.equals("Block Selector") || name.equals("See Players [ON]")
                || name.equals("See Players [OFF]") || name.equals("Parkour");
    }
}
