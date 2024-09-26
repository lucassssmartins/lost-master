package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;

public class JvmCommand extends WrappedBukkitCommand {

    public JvmCommand() {
        super("jvm");
        this.setPermission("core.cmd.jvm");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {

    }
}
