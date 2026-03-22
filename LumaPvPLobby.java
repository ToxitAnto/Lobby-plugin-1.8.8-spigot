package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TabListManager {

    private final LumaPvPLobby plugin;
    private BukkitTask task;

    public TabListManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
        start();
    }

    private void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                updateTab(p);
            }
        }, 0L, 40L);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    public void reload() {
        stop();
        start();
    }

    public void updateTab(Player player) {
        int online = plugin.getProxyManager().getOnlinePlayers();
        if (online <= 0) online = Bukkit.getOnlinePlayers().size();

        String header = c("\n&5&l  LumaPvP Network  \n &fwww.lumapvp.eu\n");
        String footer = c("\n &fOnline: &d" + online
                + " &8| &fRank: &d" + c(plugin.getConfigManager().getRank(player))
                + "\n &7discord.lumapvp.eu &8| &7store.lumapvp.eu\n");

        sendTabList(player, header, footer);
    }

    private void sendTabList(Player player, String header, String footer) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            String nms = "net.minecraft.server." + version + ".";

            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);

            Class<?> packetClass = Class.forName(nms + "PacketPlayOutPlayerListHeaderFooter");
            Object packet = packetClass.newInstance();

            Class<?> chatSerializer = Class.forName(nms + "IChatBaseComponent$ChatSerializer");
            Method fromJson = chatSerializer.getMethod("a", String.class);

            Object headerComp = fromJson.invoke(null, "{\"text\":\"" + escape(header) + "\"}");
            Object footerComp = fromJson.invoke(null, "{\"text\":\"" + escape(footer) + "\"}");

            Field fa = packetClass.getDeclaredField("a");
            fa.setAccessible(true);
            fa.set(packet, headerComp);

            Field fb = packetClass.getDeclaredField("b");
            fb.setAccessible(true);
            fb.set(packet, footerComp);

            Field connField = entityPlayer.getClass().getDeclaredField("playerConnection");
            connField.setAccessible(true);
            Object conn = connField.get(entityPlayer);

            Class<?> packetInterface = Class.forName(nms + "Packet");
            conn.getClass().getMethod("sendPacket", packetInterface).invoke(conn, packet);

        } catch (Exception ignored) {
        }
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
