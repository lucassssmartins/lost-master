package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.manager.MatchManager;
import org.bukkit.Bukkit;

public class ToggleCommand extends WrappedBukkitCommand {

    public ToggleCommand() {
        super("toggle");
        setPermission("hg.cmd.toggle");
        setAliases("var");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUso: /toggle <build/damage/pvp> <on/off>");
        } else {
            MatchManager manager = Management.getManagement(MatchManager.class);
            if (args[0].equalsIgnoreCase("build")) {
                if (args[1].equalsIgnoreCase("on")) {
                    if (!manager.isBuildEnabled()) {
                        manager.setBuildEnabled(true);
                        sender.sendMessage("§aVocê ativou as construções.");
                        MessageAPI.sendAlert(sender.getName() + " ativou as construções");
                        Bukkit.broadcastMessage("§aConstruções ativadas.");
                    } else {
                        sender.sendMessage("§cAs construções já estão ativadas.");
                    }
                } else if (args[1].equalsIgnoreCase("off")) {
                    if (manager.isBuildEnabled()) {
                        manager.setBuildEnabled(false);
                        sender.sendMessage("§aVocê desativou as construções.");
                        MessageAPI.sendAlert(sender.getName() + " desativou as construções");
                        Bukkit.broadcastMessage("§cConstruções desativadas.");
                    } else {
                        sender.sendMessage("§cAs construções já estão desativadas.");
                    }
                } else {
                    sender.sendMessage("§cUso: /toggle <build/damage/pvp> <on/off>");
                }
            } else if (args[0].equalsIgnoreCase("damage")) {
                if (args[1].equalsIgnoreCase("on")) {
                    if (!manager.isDamageEnabled()) {
                        manager.setDamageEnabled(true);
                        sender.sendMessage("§aVocê ativou o dano.");
                        MessageAPI.sendAlert(sender.getName() + " ativou o dano");
                        Bukkit.broadcastMessage("§aDano ativado.");
                    } else {
                        sender.sendMessage("§cO dano já está ativado.");
                    }
                } else if (args[1].equalsIgnoreCase("off")) {
                    if (manager.isDamageEnabled()) {
                        manager.setDamageEnabled(false);
                        sender.sendMessage("§aVocê desativou o dano.");
                        MessageAPI.sendAlert(sender.getName() + " desativou o dano");
                        Bukkit.broadcastMessage("§cDano desativado.");
                    } else {
                        sender.sendMessage("§cO dano já está desativado.");
                    }
                } else {
                    sender.sendMessage("§cUso: /toggle <build/damage/pvp> <on/off>");
                }
            } else if (args[0].equalsIgnoreCase("pvp")) {
                if (args[1].equalsIgnoreCase("on")) {
                    if (!manager.isPvpEnabled()) {
                        manager.setPvpEnabled(true);
                        sender.sendMessage("§aVocê ativou o pvp.");
                        MessageAPI.sendAlert(sender.getName() + " ativou o pvp");
                        Bukkit.broadcastMessage("§aPvP ativado.");
                    } else {
                        sender.sendMessage("§cO pvp já está ativado.");
                    }
                } else if (args[1].equalsIgnoreCase("off")) {
                    if (manager.isPvpEnabled()) {
                        manager.setPvpEnabled(false);
                        sender.sendMessage("§aVocê desativou o pvp.");
                        MessageAPI.sendAlert(sender.getName() + " desativou o pvp");
                        Bukkit.broadcastMessage("§cPvP desativado.");
                    } else {
                        sender.sendMessage("§cO pvp já está desativado.");
                    }
                } else {
                    sender.sendMessage("§cUso: /toggle <build/damage/pvp> <on/off>");
                }
            } else {
                sender.sendMessage("§cUso: /toggle <build/damage/pvp> <on/off>");
            }
        }
    }
}
