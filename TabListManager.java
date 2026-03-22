package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {

    private final LumaPvPLobby plugin;

    public ConfigManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public boolean isLobbySet() {
        return cfg().contains("lobby.world");
    }

    public boolean isParkourSet() {
        return cfg().contains("parkour.world");
    }

    public Location getLobbyLocation() {
        if (!isLobbySet()) return null;
        World w = plugin.getServer().getWorld(cfg().getString("lobby.world"));
        if (w == null) return null;
        return new Location(w,
                cfg().getDouble("lobby.x"), cfg().getDouble("lobby.y"), cfg().getDouble("lobby.z"),
                (float) cfg().getDouble("lobby.yaw"), (float) cfg().getDouble("lobby.pitch"));
    }

    public void setLobbyLocation(Location loc) {
        cfg().set("lobby.world", loc.getWorld().getName());
        cfg().set("lobby.x", loc.getX());
        cfg().set("lobby.y", loc.getY());
        cfg().set("lobby.z", loc.getZ());
        cfg().set("lobby.yaw", (double) loc.getYaw());
        cfg().set("lobby.pitch", (double) loc.getPitch());
        plugin.saveConfig();
    }

    public Location getParkourLocation() {
        if (!isParkourSet()) return null;
        World w = plugin.getServer().getWorld(cfg().getString("parkour.world"));
        if (w == null) return null;
        return new Location(w,
                cfg().getDouble("parkour.x"), cfg().getDouble("parkour.y"), cfg().getDouble("parkour.z"),
                (float) cfg().getDouble("parkour.yaw"), (float) cfg().getDouble("parkour.pitch"));
    }

    public void setParkourLocation(Location loc) {
        cfg().set("parkour.world", loc.getWorld().getName());
        cfg().set("parkour.x", loc.getX());
        cfg().set("parkour.y", loc.getY());
        cfg().set("parkour.z", loc.getZ());
        cfg().set("parkour.yaw", (double) loc.getYaw());
        cfg().set("parkour.pitch", (double) loc.getPitch());
        plugin.saveConfig();
    }

    public String getRank(Player player) {
        String lp = getLuckPermsPrefix(player);
        if (lp != null && !lp.isEmpty()) return lp;
        if (player.hasPermission("lumapvp.rank.admin")) return cfg().getString("ranks.admin", "&c[Admin]");
        if (player.hasPermission("lumapvp.rank.staff")) return cfg().getString("ranks.staff", "&b[Staff]");
        if (player.hasPermission("lumapvp.rank.mvp"))   return cfg().getString("ranks.mvp",   "&6[MVP]");
        if (player.hasPermission("lumapvp.rank.vip"))   return cfg().getString("ranks.vip",   "&a[VIP]");
        return cfg().getString("ranks.default", "&7[Player]");
    }

    private String getLuckPermsPrefix(Player player) {
        try {
            Object lp = plugin.getServer().getServicesManager()
                    .getRegistration(Class.forName("net.luckperms.api.LuckPerms")).getProvider();
            Object um   = lp.getClass().getMethod("getUserManager").invoke(lp);
            Object user = um.getClass().getMethod("getUser", UUID.class).invoke(um, player.getUniqueId());
            if (user == null) return null;
            Object cd   = user.getClass().getMethod("getCachedData").invoke(user);
            Object md   = cd.getClass().getMethod("getMetaData").invoke(cd);
            return (String) md.getClass().getMethod("getPrefix").invoke(md);
        } catch (Exception e) {
            return null;
        }
    }

    public int getBlocksAmount()        { return cfg().getInt("blocks.amount", 64); }
    public int getBlocksDisappearDelay(){ return cfg().getInt("blocks.disappear-delay", 5); }

    public int getSlot(String key, int def) { return cfg().getInt("hotbar." + key, def); }
    public int getParkourSlot()        { return getSlot("parkour-slot", 2); }
    public int getVisibilitaSlot()     { return getSlot("visibilita-slot", 3); }
    public int getSelettoreBloccoSlot(){ return getSlot("selettore-blocco-slot", 4); }
    public int getSelectorSlot()       { return getSlot("selettore-slot", 5); }
    public int getProfileSlot()        { return getSlot("profilo-slot", 7); }
    public int getSettingsSlot()       { return getSlot("impostazioni-slot", 8); }

    public boolean isInventoryProtectionEnabled() { return cfg().getBoolean("inventory-protection.enabled", true); }
    public boolean isNoDrop()                      { return cfg().getBoolean("inventory-protection.no-drop", true); }

    public List<Material> getSelectableBlocks() {
        List<String> names = cfg().getStringList("block-selector.blocks");
        List<Material> mats = new ArrayList<>();
        for (String n : names) {
            try { mats.add(Material.valueOf(n)); } catch (Exception ignored) {}
        }
        if (mats.isEmpty()) mats.add(Material.QUARTZ_BLOCK);
        return mats;
    }
}
