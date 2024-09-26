package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InvseeCommand extends WrappedBukkitCommand {

    public InvseeCommand() {
        super("invsee");
        setPermission("core.cmd.invsee");
        setAliases("inv");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§cUso: /invsee <target>");
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    p.openInventory(target.getInventory());
                    p.sendMessage("§aVisualizando inventário de " + target.getName() + "!");
                    MessageAPI.sendAlert(p.getName() + " abriu o inventário de " + target.getName());
                } else {
                    p.sendMessage("§cJogador não encontrado.");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
