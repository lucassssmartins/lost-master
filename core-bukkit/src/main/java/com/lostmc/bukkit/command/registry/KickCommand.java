package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KickCommand extends WrappedBukkitCommand {

    public KickCommand() {
        super("kick");
        setPermission("core.cmd.kick");
        setAliases("kickar", "expulsar");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /kick <target> <reason>");
        } else {
            Player t = Bukkit.getPlayer(args[0]);
            if (t != null) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    String space = " ";
                    if (i >= args.length - 1)
                        space = "";
                    builder.append(args[i] + space);
                }
                if (builder.length() == 0) {
                    builder.append("não informado");
                }
                sender.sendMessage("§aVocê expulsou o jogador " + t.getName() + " pelo motivo " + builder);
                t.kickPlayer("§cVoce foi expulso do servidor\nMotivo: " + builder);
                MessageAPI.sendAlert(sender.getName() + " expulsou " + t.getName() + " do servidor pelo motivo "
                        + builder);
            } else {
                sender.sendMessage("§cJogador não encontrado.");
            }
        }
    }
}
