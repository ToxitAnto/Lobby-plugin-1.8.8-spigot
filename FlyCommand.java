package it.lumapvp.lobby.managers;
import org.bukkit.entity.Player;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class ActionBarManager {
    public static void send(Player player, String message) {
        try {
            String version = player.getServer().getClass().getPackage().getName().split("\\.")[3];
            String nmsPackage = "net.minecraft.server." + version;
            String cbPackage = "org.bukkit.craftbukkit." + version;
            Class<?> chatSerializerClass = Class.forName(nmsPackage + ".ChatSerializer");
            Method aMethod = chatSerializerClass.getDeclaredMethod("a", String.class);
            Object chatComponent = aMethod.invoke(null, "{\"text\":\"" + message.replace("\"", "\\\"") + "\"}");
            Class<?> packetClass = Class.forName(nmsPackage + ".PacketPlayOutChat");
            Class<?> iChatBaseComponent = Class.forName(nmsPackage + ".IChatBaseComponent");
            Constructor<?> packetConstructor = packetClass.getDeclaredConstructor(iChatBaseComponent, byte.class);
            Object packet = packetConstructor.newInstance(chatComponent, (byte) 2);
            Class<?> craftPlayerClass = Class.forName(cbPackage + ".entity.CraftPlayer");
            Method getHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object entityPlayer = getHandleMethod.invoke(player);
            Field playerConnectionField = entityPlayer.getClass().getDeclaredField("playerConnection");
            playerConnectionField.setAccessible(true);
            Object playerConnection = playerConnectionField.get(entityPlayer);
            Class<?> packetInterface = Class.forName(nmsPackage + ".Packet");
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetInterface);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            player.sendMessage(message);
        }
    }
}
