package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;

public class SkinCommand extends WrappedProxyCommand {

    public SkinCommand(String name) {
        super(name);
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {

    }
}
