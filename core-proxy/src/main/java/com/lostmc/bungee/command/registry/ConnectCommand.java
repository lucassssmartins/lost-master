package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ConnectCommand extends WrappedProxyCommand {

    public ConnectCommand() {
        super("connect");
        setPermission("core.cmd.staffchat");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage(new TextComponent("§cUso: /connect <server-id>"));
            } else {
                ServerInfo info = ProxyServer.getInstance().getServerInfo(args[0]);
                if (info != null) {
                    p.connect(info);
                } else {
                    p.sendMessage(TextComponent.fromLegacyText("§cServidor não encontrado."));
                }
            }
        }
    }
}
