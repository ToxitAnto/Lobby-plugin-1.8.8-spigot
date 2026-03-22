package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class InventoryManager {

    private final LumaPvPLobby plugin;

    public InventoryManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    private String c(String s) { return ChatColor.translateAlternateColorCodes('&', s); }

    private ItemStack make(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(c(name));
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(c(s));
        meta.setLore(l);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack makeWithLore(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(c(name));
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(c(s));
        meta.setLore(l);
        item.setItemMeta(meta);
        return item;
    }

    @SuppressWarnings("deprecation")
    private ItemStack playerSkull(Player player, String name, String... lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(c(name));
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(c(s));
        meta.setLore(l);
        skull.setItemMeta(meta);
        return skull;
    }

    @SuppressWarnings("deprecation")
    private ItemStack glass(int data) {
        ItemStack p = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) data);
        ItemMeta m = p.getItemMeta(); m.setDisplayName(" "); p.setItemMeta(m);
        return p;
    }

    private void border54(Inventory inv) {
        ItemStack b = glass(10);
        for (int i = 0; i < 9; i++) inv.setItem(i, b);
        for (int i = 45; i < 54; i++) inv.setItem(i, b);
        for (int i = 9; i < 45; i += 9) inv.setItem(i, b);
        for (int i = 17; i < 54; i += 9) inv.setItem(i, b);
    }

    private void border27(Inventory inv) {
        ItemStack b = glass(10);
        for (int i = 0; i < 9; i++) inv.setItem(i, b);
        for (int i = 18; i < 27; i++) inv.setItem(i, b);
        inv.setItem(9, b); inv.setItem(17, b);
    }

    public Inventory buildSelectorMenu(Player player) {
        String title = c(plugin.getConfig().getString("selector.title", "&5&lLumaPvP &7- &fSelector"));
        Inventory inv = Bukkit.createInventory(null, 54, title);
        border54(inv);
        inv.setItem(22, makeWithLore(Material.BED,
                plugin.getConfig().getString("selector.bedwars.name", "&e&lBedWars"),
                plugin.getConfig().getStringList("selector.bedwars.lore")));
        inv.setItem(20, makeWithLore(Material.PAPER,
                plugin.getConfig().getString("selector.support.name", "&c&lSupport"),
                plugin.getConfig().getStringList("selector.support.lore")));
        inv.setItem(24, make(Material.BOOK, "&b&lUseful Links",
                "", "&fwww.lumapvp.eu", "&fdiscord.lumapvp.eu", "&fstore.lumapvp.eu", ""));
        return inv;
    }

    public Inventory buildProfileMenu(Player player) {
        String title = c(plugin.getConfig().getString("profile.title", "&5&lLumaPvP &7- &fProfile"));
        Inventory inv = Bukkit.createInventory(null, 54, title);
        border54(inv);
        String rank = plugin.getConfigManager().getRank(player);
        inv.setItem(22, playerSkull(player,
                "&5» &f" + player.getName() + " &8| " + rank,
                "&7&m--------------------",
                "&fRank: " + c(rank),
                "&7&m--------------------"));
        int w = plugin.getPlayerDataManager().getStat(player, "bw-wins");
        int g = plugin.getPlayerDataManager().getStat(player, "bw-played");
        int b = plugin.getPlayerDataManager().getStat(player, "bw-beds");
        int k = plugin.getPlayerDataManager().getStat(player, "bw-kills");
        inv.setItem(29, make(Material.BED, "&e&lBedWars Stats",
                "", "&7Wins: &d" + w, "&7Games: &d" + g,
                "&7Beds destroyed: &d" + b, "&7Kills: &d" + k, ""));
        inv.setItem(31, make(Material.WATCH, "&b&lFirst Join",
                "", "&f" + plugin.getPlayerDataManager().getFirstJoin(player), ""));
        inv.setItem(33, make(Material.BOOK, "&a&lPlaytime",
                "", "&f" + plugin.getPlayerDataManager().getFormattedPlaytime(player), ""));
        return inv;
    }

    public Inventory buildSettingsMenu(Player player) {
        String title = c(plugin.getConfig().getString("settings.title", "&5&lLumaPvP &7- &fSettings"));
        return Bukkit.createInventory(null, 27, title);
    }

    public Inventory buildBlockSelectorMenu(Player player) {
        String title = c(plugin.getConfig().getString("block-selector.title", "&5&lSelect a block"));
        Inventory inv = Bukkit.createInventory(null, 27, title);
        border27(inv);
        List<Material> blocks = plugin.getBlockSelectorManager().getAvailableBlocks();
        Material sel = plugin.getBlockSelectorManager().getSelectedBlock(player);
        int[] slots = {10, 11, 13, 15, 16};
        for (int i = 0; i < blocks.size() && i < slots.length; i++) {
            Material mat = blocks.get(i);
            boolean isSel = mat == sel;
            inv.setItem(slots[i], make(mat,
                    BlockSelectorManager.getBlockName(mat) + (isSel ? " &a\u2714" : ""),
                    "", isSel ? "&a[SELECTED]" : "&7Click to select!", ""));
        }
        return inv;
    }

    public void giveHotbarItems(Player player) {
        player.getInventory().clear();
        ConfigManager cm = plugin.getConfigManager();
        boolean hiding = plugin.getVisibilityManager().isHiding(player);

        player.getInventory().setItem(cm.getParkourSlot(),
                make(Material.FEATHER, "&5&lParkour",
                        "", "&7Teleport to the Parkour!", "", "&eRight-click!"));

        player.getInventory().setItem(cm.getVisibilitaSlot(),
                make(Material.EYE_OF_ENDER,
                        hiding ? "&7&lSee Players &8[OFF]" : "&5&lSee Players &a[ON]",
                        "", hiding ? "&7Players are &chidden&7." : "&7Players are &avisible&7.",
                        "", "&eClick to toggle!"));

        player.getInventory().setItem(cm.getSelettoreBloccoSlot(),
                make(Material.DIODE, "&5&lBlock Selector",
                        "", "&7Select your block type!", "", "&eRight-click to open!"));

        player.getInventory().setItem(cm.getSelectorSlot(),
                make(Material.NETHER_STAR, "&5&lSelector",
                        "", "&7Choose your game mode!", "", "&eRight-click to open!"));

        player.getInventory().setItem(cm.getProfileSlot(),
                playerSkull(player, "&5&lProfile",
                        "", "&7View your profile!", "", "&eRight-click to open!"));

        player.getInventory().setItem(cm.getSettingsSlot(),
                make(Material.COMPASS, "&5&lSettings",
                        "", "&7Customize your experience!", "", "&eRight-click to open!"));

        Material blockMat = plugin.getBlockSelectorManager().getSelectedBlock(player);
        player.getInventory().setItem(0, new ItemStack(blockMat, cm.getBlocksAmount()));

        player.updateInventory();
    }

    public void updateVisibilityItem(Player player) {
        boolean hiding = plugin.getVisibilityManager().isHiding(player);
        int slot = plugin.getConfigManager().getVisibilitaSlot();
        player.getInventory().setItem(slot, make(Material.EYE_OF_ENDER,
                hiding ? "&7&lSee Players &8[OFF]" : "&5&lSee Players &a[ON]",
                "", hiding ? "&7Players are &chidden&7." : "&7Players are &avisible&7.",
                "", "&eClick to toggle!"));
        player.updateInventory();
    }
}
