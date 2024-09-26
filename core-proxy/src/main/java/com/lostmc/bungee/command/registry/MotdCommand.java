package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.bungee.manager.MotdManager;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.api.ChatColor;

public class MotdCommand extends WrappedProxyCommand {

    public MotdCommand() {
        super("motd");
        this.setPermission("*");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 1) {
            sender.tlMessage("command.motd.usage");
        } else {
            MotdManager manager = ProxyPlugin.getInstance().getMotdManager();
            if (args[0].equalsIgnoreCase("header")) {
                String header = ChatColor.translateAlternateColorCodes('&', createArgs(1, args));
                manager.saveDefaultHeaderMotd(header);
                sender.tlMessage("command.motd.header-seted", header);
            } else if (args[0].equalsIgnoreCase("footer")) {
                String footer = ChatColor.translateAlternateColorCodes('&', createArgs(1, args));
                manager.saveDefaultFooterMotd(footer);
                sender.tlMessage("command.motd.footer-seted", footer);
            } else {
                sender.tlMessage("command.motd.usage");
            }
        }
    }

    private String createArgs(int begin, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < args.length; i++)
            sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
        return sb.toString();
    }
}
