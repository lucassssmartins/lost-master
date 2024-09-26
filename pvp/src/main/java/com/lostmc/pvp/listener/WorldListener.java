package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener extends BukkitListener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }
}
