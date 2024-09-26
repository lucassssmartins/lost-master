package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TpCommand extends WrappedBukkitCommand {

    public TpCommand() {
        super("tp");
        setPermission("core.cmd.tp");
        setAliases("teleport");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§cUso: /tp <player> [target] and/or (x) (y) (z)");
            } else if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    p.teleport(t.getLocation());
                    p.sendMessage("§aTeleportado para " + t.getName() + " com sucesso!");
                    MessageAPI.sendAlert(p.getName() + " teleportou para " + t.getName());
                } else {
                    p.sendMessage("§cJogador não encontrado.");
                }
            } else if (args.length == 2) {
                Player t1 = Bukkit.getPlayer(args[0]);
                Player t2 = Bukkit.getPlayer(args[1]);
                if (t1 != null) {
                    if (t2 != null) {
                        t1.teleport(t2.getLocation());
                        p.sendMessage("§aTeleportado " + t1.getName() + " para " + t2.getName() + " com sucesso!");
                        MessageAPI.sendAlert(p.getName() + " teleportou " + t1.getName() + " para " + t2.getName());
                    } else {
                        p.sendMessage("§cO jogador " + args[1] + " não foi encontrado.");
                    }
                } else {
                    p.sendMessage("§cO jogador " + args[0] + " não foi encontrado.");
                }
            } else if (args.length == 3) {
                final int x, y, z;
                try {
                    x = Integer.parseInt(args[0]);
                    y = Integer.parseInt(args[1]);
                    z = Integer.parseInt(args[2]);
                    Location teleport = new Location(p.getWorld(), x, y, z);
                    p.teleport(teleport);
                    p.sendMessage("§aTeleportado para " + x + ", " + y + ", " + z + " com sucesso!");
                   MessageAPI.sendAlert(p.getName() + " teleportou para " + x + ", " + y + ", " + z);
                } catch (NumberFormatException e) {
                    p.sendMessage("§cCoordenadas inválidas.");
                }
            } else if (args.length >= 4) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    final int x, y, z;
                    try {
                        x = Integer.parseInt(args[1]);
                        y = Integer.parseInt(args[2]);
                        z = Integer.parseInt(args[3]);
                        Location teleport = new Location(p.getWorld(), x, y, z);
                        t.teleport(teleport);
                        p.sendMessage("§aTeleportado " + t.getName() + " para " + x + ", " + y + ", " + z + " com sucesso!");
                        MessageAPI.sendAlert(p.getName() + " teleportou " + t.getName() + " para " + x + ", " + y + ", " + z);
                    } catch (NumberFormatException e) {
                        p.sendMessage("§cCoordenadas inválidas.");
                    }
                    t = null;
                } else {
                    p.sendMessage("§cO jogador " + args[0] + " não foi encontrado.");
                }
            }
            p = null;
        } else {
            sender.sendInGameMessage();
        }
    }
}
