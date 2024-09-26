package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.sidebar.HungerSidebarModel;
import org.bukkit.ChatColor;

public class TitleCommand extends WrappedBukkitCommand {

    public TitleCommand() {
        super("title");
        setPermission("hg.cmd.title");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /title <name>");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++)
                sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
            HungerSidebarModel.setSidebarTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + sb);
        }
    }
}
