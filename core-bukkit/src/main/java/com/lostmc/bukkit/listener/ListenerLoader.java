package com.lostmc.bukkit.listener;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.core.utils.ClassGetter;

import java.io.File;
import java.util.logging.Level;

public class ListenerLoader {

    private File jarFile;
    private BukkitPlugin plugin;

    public ListenerLoader(File jarFile, BukkitPlugin plugin) {
        this.jarFile = jarFile;
        this.plugin = plugin;
    }

    public int loadListeners(String pkgName) {
        int i = 0;

        for (Class<?> clazz : ClassGetter.getClassesForPackageByFile(this.jarFile, pkgName)) {
            if (!BukkitListener.class.isAssignableFrom(clazz))
                continue;
            try {
                ++i;
                ((BukkitListener) clazz.getConstructor().newInstance()).register((BukkitPlugin) this.plugin);
                this.plugin.getLogger().info("Listener '" + clazz.getSimpleName() + "' registered.");
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.INFO, "Listener '" + clazz.getSimpleName() +
                        "' not registered:", e);
                continue;
            }
        }

        return i;
    }
}
