package it.lumapvp.lobby.listeners;

import it.lumapvp.lobby.LumaPvPLobby;
import it.lumapvp.lobby.managers.BlockSelectorManager;
import it.lumapvp.lobby.managers.InventoryManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final LumaPvPLobby plugin;
    private final InventoryManager inv;
    private final String pre;

    public InventoryListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
        this.inv = new InventoryManager(plugin);
        this.pre = ChatColor.translateAlternateColorCodes('&', "&5[LumaPvP] &f");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;

        org.bukkit.event.block.Action a = event.getAction();
        if (a != org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                && a != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        String raw = p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()
                ? p.getItemInHand().getItemMeta().getDisplayName() : "";
        String name = ChatColor.stripColor(raw);

        switch (name) {
            case "Selector":
                event.setCancelled(true);
                p.openInventory(inv.buildSelectorMenu(p));
                break;
            case "Profile":
                event.setCancelled(true);
                p.openInventory(inv.buildProfileMenu(p));
                break;
            case "Settings":
                event.setCancelled(true);
                p.openInventory(inv.buildSettingsMenu(p));
                break;
            case "Block Selector":
                event.setCancelled(true);
                p.openInventory(inv.buildBlockSelectorMenu(p));
                break;
            case "See Players [ON]":
            case "See Players [OFF]":
                event.setCancelled(true);
                plugin.getVisibilityManager().toggleVisibility(p);
                inv.updateVisibilityItem(p);
                p.sendMessage(pre + (plugin.getVisibilityManager().isHiding(p)
                        ? ChatColor.GRAY + "Players hidden."
                        : ChatColor.LIGHT_PURPLE + "Players visible."));
                break;
            case "Parkour":
                event.setCancelled(true);
                Location pk = plugin.getConfigManager().getParkourLocation();
                if (pk != null) {
                    p.teleport(pk);
                    p.sendMessage(pre + ChatColor.LIGHT_PURPLE + "Teleported to Parkour!");
                } else {
                    p.sendMessage(pre + ChatColor.RED + "Parkour has not been set yet!");
                }
                break;
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        String title = event.getInventory().getName();

        if (title.contains("Selector") && !title.contains("Block")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            String name = event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()
                    ? ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()) : "";
            if (name.contains("BedWars")) {
                p.closeInventory();
                plugin.getProxyManager().connectToServer(p,
                        plugin.getConfig().getString("selector.bedwars.server", "bedwars"));
                p.sendMessage(pre + ChatColor.YELLOW + "Connecting to BedWars...");
            }
            return;
        }

        if (title.contains("Settings") || title.contains("Profile")) {
            event.setCancelled(true);
            return;
        }

        if (title.contains("Select a block")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) return;
            Material clicked = event.getCurrentItem().getType();
            plugin.getBlockSelectorManager().setSelectedBlock(p, clicked);
            int amount = plugin.getConfigManager().getBlocksAmount();
            p.getInventory().setItem(0, new ItemStack(clicked, amount));
            p.updateInventory();
            p.closeInventory();
            p.openInventory(inv.buildBlockSelectorMenu(p));
            p.sendMessage(pre + ChatColor.GREEN + "Block changed: "
                    + ChatColor.translateAlternateColorCodes('&', BlockSelectorManager.getBlockName(clicked)));
        }
    }
}
