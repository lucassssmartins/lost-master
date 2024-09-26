package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class ShowDifficulty extends WrappedBukkitCommand {

    public ShowDifficulty() {
        super("showdifficulty");
        setPermission("*");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        sender.sendMessage("Hardcore: " + ((CraftWorld) Bukkit.getWorld("world")).getHandle().worldData.isHardcore());
    }
}
