package com.lostmc.bukkit.command;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.core.utils.ClassGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.io.File;

public class CommandLoader {

    private File jarFile;
    private CommandMap map;

    public CommandLoader(File jarFile) {
        this.jarFile = jarFile;
        this.map = Bukkit.getCommandMap();
    }

    public int loadCommands(String pkgName) {
        int i = 0;

        for (Class<?> cmdClass : ClassGetter.getClassesForPackageByFile(this.jarFile, pkgName)) {
            if (!WrappedBukkitCommand.class.isAssignableFrom(cmdClass))
                continue;
            try {
                ++i;
                ((WrappedBukkitCommand) cmdClass.getConstructor().newInstance()).register(this.map);
                BukkitPlugin.getInstance().getLogger().info("Command '" + cmdClass.getSimpleName() + "' registered.");
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        return i;
    }
}
