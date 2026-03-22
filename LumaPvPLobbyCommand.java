package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlockSelectorManager {

    private final LumaPvPLobby plugin;
    private final Map<UUID, Material> selected = new HashMap<>();

    public BlockSelectorManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    public Material getSelectedBlock(Player player) {
        List<Material> avail = plugin.getConfigManager().getSelectableBlocks();
        Material mat = selected.getOrDefault(player.getUniqueId(), avail.get(0));
        if (!avail.contains(mat)) mat = avail.get(0);
        return mat;
    }

    public void setSelectedBlock(Player player, Material mat) {
        selected.put(player.getUniqueId(), mat);
    }

    public List<Material> getAvailableBlocks() {
        return plugin.getConfigManager().getSelectableBlocks();
    }

    public static String getBlockName(Material mat) {
        switch (mat) {
            case QUARTZ_BLOCK:  return "&f&lQuartz";
            case WOOL:          return "&f&lWhite Wool";
            case STAINED_CLAY:  return "&5&lPurple Clay";
            case OBSIDIAN:      return "&8&lObsidian";
            case SANDSTONE:     return "&e&lSandstone";
            case IRON_BLOCK:    return "&7&lIron";
            case GOLD_BLOCK:    return "&6&lGold";
            case DIAMOND_BLOCK: return "&b&lDiamond";
            case EMERALD_BLOCK: return "&a&lEmerald";
            case STONE:         return "&8&lStone";
            default:            return "&7" + mat.name();
        }
    }
}
