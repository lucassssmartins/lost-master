package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class CheatingBanCommand extends WrappedProxyCommand {

    public CheatingBanCommand() {
        super("cban");
        setPermission("core.cmd.punish");
        setAliases("cheatingban");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUso: /cban <target> <reason>");
        } else {
            ProxyServer.getInstance().getScheduler().schedule(ProxyPlugin.getInstance(), () -> {
                BungeeCord.getInstance().getPluginManager()
                        .dispatchCommand((CommandSender) sender.getHandle(), "p ban cheating n "
                                + args[0] + " " + createArgs(1, args));
            }, 1, TimeUnit.MILLISECONDS);
        }
    }

    private String createArgs(int begin, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < args.length; i++)
            sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
        return sb.toString();
    }
}
