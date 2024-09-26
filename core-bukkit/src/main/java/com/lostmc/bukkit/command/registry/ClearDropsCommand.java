package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.utils.entity.EntityUtils;
import com.lostmc.core.command.WrappedCommandSender;

public class ClearDropsCommand extends WrappedBukkitCommand {

    public ClearDropsCommand() {
        super("cleardrops");
        setPermission("core.cmd.cleardrops");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        EntityUtils.clearDrops();
        MessageAPI.sendAlert(sender.getName() + " usou /cleardrops");
        sender.sendMessage("§aVocê removeu os itens do chão!");
    }
}
