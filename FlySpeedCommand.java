package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockAnimationManager {

    private final LumaPvPLobby plugin;

    public BlockAnimationManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    public void giveBlocks(Player player) {
        int amount = plugin.getConfigManager().getBlocksAmount();
        Material mat = plugin.getBlockSelectorManager().getSelectedBlock(player);
        player.getInventory().addItem(new ItemStack(mat, amount));
        player.updateInventory();
    }
}
