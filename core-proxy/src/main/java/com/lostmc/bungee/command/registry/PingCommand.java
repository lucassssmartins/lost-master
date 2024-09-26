package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PingCommand extends WrappedProxyCommand {

    public PingCommand() {
        super("ping");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§eSeu ping é de §6" + p.getPing() + "§e ms.");
            } else {
                ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);
                if (t != null) {
                    p.sendMessage("§eO ping de §6" + t.getName() + "§e é de §6" + t.getPing() + "§e ms.");
                } else {
                    p.sendMessage("§cJogador não encontrado.");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
