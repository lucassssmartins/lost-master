package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class BroadcastCommand extends WrappedProxyCommand {

    public BroadcastCommand() {
        super("broacast");
        setPermission("core.cmd.broadcast");
        setAliases("bc", "aviso", "anuncio");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /broadcast <message>");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++)
                sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
            ProxyServer.getInstance().broadcast(
                    new TextComponent("§6§lLOST §7» §f" + sb.toString().replace('&', '§')));
        }
    }
}
