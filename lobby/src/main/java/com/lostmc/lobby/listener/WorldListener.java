package com.lostmc.lobby.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Weather;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener extends BukkitListener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }
}
