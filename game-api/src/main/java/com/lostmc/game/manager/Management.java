package com.lostmc.game.manager;

import com.lostmc.game.GamePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public abstract class Management {

    private static final Map<Class<? extends Management>, Management> managements = new HashMap<>();

    @Getter
    private GamePlugin plugin;

    public abstract void onEnable();

    public abstract void onDisable();

    public Server getServer() {
        return getPlugin().getServer();
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, getPlugin());
    }

    public static <T extends Management> T getManagement(Class<T> tClass) {
        return tClass.cast(managements.get(tClass));
    }

    public static boolean enableManagement(Class<? extends Management> c) {
        if (!managements.containsKey(c)) {
            try {
                Management management = c.getConstructor(GamePlugin.class).newInstance(GamePlugin.getInstance());
                management.onEnable();
                managements.put(c, management);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean disableManagement(Class<? extends Management> c) {
        Management management = managements.remove(c);
        if (management != null) {
            management.onDisable();
            return true;
        }
        return false;
    }
}
