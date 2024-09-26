package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MojangCommand extends WrappedProxyCommand {

    public MojangCommand() {
        super("mojang");
        setPermission("core.cmd.staffchat");
        setAliases("network");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        sender.sendMessage("§9Estatisticas de jogadores §6originais§9 e §epiratas§9:");

        for (ServerInfo server : BungeeCord.getInstance().getServers().values()) {
            int crackedCount = 0, premiumCount = 0;

            for (ProxiedPlayer o : server.getPlayers()) {

                if (o.getPendingConnection().isOnlineMode()) {
                    premiumCount++;
                } else {
                    crackedCount++;
                }
            }

            sender.sendMessage("§9" + server.getName().toUpperCase() + ": §6" + premiumCount + "§9/§e" + crackedCount);
        }

        int crackedCount = 0, premiumCount = 0, onlineCount = 0;

        for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {

            if (o.getPendingConnection().isOnlineMode()) {
                premiumCount++;
            } else {
                crackedCount++;
            }

            onlineCount++;
        }

        sender.sendMessage("§9Infomações gerais:");

        sender.sendMessage("§6" + premiumCount + " jogadores originais");
        sender.sendMessage("§e" + crackedCount + " jogadores piratas");
        sender.sendMessage("§a" + onlineCount + " jogadores totais online");
    }
}
