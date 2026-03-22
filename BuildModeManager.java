package it.lumapvp.lobby.listeners;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockPlaceListener implements Listener {

    private final LumaPvPLobby plugin;
    private final Map<UUID, List<org.bukkit.block.Block>> placedBlocks = new ConcurrentHashMap<>();

    public BlockPlaceListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("lumapvp.admin") && plugin.getBuildModeManager().isBuildEnabled()) return;

        org.bukkit.block.Block block = event.getBlock();
        UUID uuid = player.getUniqueId();

        placedBlocks.computeIfAbsent(uuid, k -> Collections.synchronizedList(new ArrayList<>())).add(block);

        int delay = plugin.getConfigManager().getBlocksDisappearDelay();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            List<org.bukkit.block.Block> blocks = placedBlocks.get(uuid);
            if (blocks == null || !blocks.contains(block)) return;

            blocks.remove(block);
            if (blocks.isEmpty()) placedBlocks.remove(uuid);

            if (block.getType() == Material.AIR) return;

            Material mat = block.getType();

            new BukkitRunnable() {
                int step = 0;
                final Material[] anim = {mat, Material.AIR, mat, Material.AIR, Material.AIR};

                @Override
                public void run() {
                    if (step >= anim.length) {
                        cancel();
                        block.setType(Material.AIR);
                        if (player.isOnline()) {
                            int amount = plugin.getConfigManager().getBlocksAmount();
                            player.getInventory().setItem(0, new ItemStack(mat, amount));
                            player.updateInventory();
                        }
                        return;
                    }
                    block.setType(anim[step]);
                    step++;
                }
            }.runTaskTimer(plugin, 0L, 4L);

        }, delay * 20L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("lumapvp.admin") && plugin.getBuildModeManager().isBuildEnabled()) return;
        if (player.hasPermission("lumapvp.staff")) return;
        event.setCancelled(true);
    }
}
