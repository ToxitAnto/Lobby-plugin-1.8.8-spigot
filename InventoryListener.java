package it.lumapvp.lobby.listeners;

import it.lumapvp.lobby.LumaPvPLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    private final LumaPvPLobby plugin;

    public WorldListener(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (plugin.getConfig().getBoolean("world.no-rain", true)) {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        }
    }
}
