package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.kit.registry.Nenhum;
import com.lostmc.hungergames.manager.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ToggleKitCommand extends WrappedBukkitCommand {

    public ToggleKitCommand() {
        super("togglekit");
        setPermission("hg.cmd.togglekit");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUso: /togglekit <kit/all> <on/off>");
        } else {
            if (args[0].equalsIgnoreCase("all")) {
                if (args[1].equalsIgnoreCase("on")) {
                    for (Kit kit : Management.getManagement(KitManager.class).getAllKits()) {
                        if (!(kit instanceof Nenhum)) {
                            kit.setActived(true);
                        }
                    }
                    sender.sendMessage("§aVocê habilitou todos os kits.");
                    MessageAPI.sendAlert(sender.getName() + " habilitou todos os kits");
                    Bukkit.broadcastMessage("§aTodos os kits foram ativados.");
                } else if (args[1].equalsIgnoreCase("off")) {
                    for (Kit kit : Management.getManagement(KitManager.class).getAllKits()) {
                        if (!(kit instanceof Nenhum)) {
                            kit.setActived(false);
                        }
                    }
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        HungerGamer gamer = HungerGamer.getGamer(ps);
                        if (gamer.isAlive()) {
                            gamer.getKits().remove(1);
                            gamer.getKits().remove(2);
                        }
                    }
                    sender.sendMessage("§aVocê desabilitou todos os kits.");
                    MessageAPI.sendAlert(sender.getName() + " desabilitou todos os kits");
                    Bukkit.broadcastMessage("§cTodos os kits foram desativados.");
                } else {
                    sender.sendMessage("§cUso: /togglekit <kit/all> <on/off>");
                }
            } else {
                Kit kit = Management.getManagement(KitManager.class).getKitByName(args[0]);
                if (kit != null) {
                    if (args[1].equalsIgnoreCase("on")) {
                        if (!kit.isActived()) {
                            kit.setActived(true);
                            for (Player ps : Bukkit.getOnlinePlayers()) {
                                HungerGamer gamer = HungerGamer.getGamer(ps);
                                if (gamer.isAlive()) {
                                    if (gamer.hasPrimaryKit() && gamer.getPrimaryKit().equals(kit))
                                        gamer.setPrimaryKit(null);
                                    if (gamer.hasSecondaryKit() && gamer.getSecondaryKit().equals(kit))
                                        gamer.setSecondaryKit(null);
                                }
                            }
                            sender.sendMessage("§aVocê habilitou o kit " + kit.getName() + ".");
                            MessageAPI.sendAlert(sender.getName() + " habilitou o kit " + kit.getName());
                            Bukkit.broadcastMessage("§aKit " + kit.getName() + " ativado.");
                        } else {
                            sender.sendMessage("§cO kit " + kit.getName() + " já está ativado.");
                        }
                    } else if (args[1].equalsIgnoreCase("off")) {
                        if (kit.isActived()) {
                            kit.setActived(false);
                            for (Player ps : Bukkit.getOnlinePlayers()) {
                                HungerGamer gamer = HungerGamer.getGamer(ps);
                                if (gamer.isAlive()) {
                                    if (gamer.hasPrimaryKit() && gamer.getPrimaryKit().equals(kit))
                                        gamer.setPrimaryKit(null);
                                    if (gamer.hasSecondaryKit() && gamer.getSecondaryKit().equals(kit))
                                        gamer.setSecondaryKit(null);
                                }
                            }
                            sender.sendMessage("§aVocê desabilitou o kit " + kit.getName() + ".");
                            MessageAPI.sendAlert(sender.getName() + " desabilitou o kit " + kit.getName());
                            Bukkit.broadcastMessage("§cKit " + kit.getName() + " desativado.");
                        } else {
                            sender.sendMessage("§cO kit " + kit.getName() + " já está desativado.");
                        }
                    } else {
                        sender.sendMessage("§cUso: /togglekit <kit/all> <on/off>");
                    }
                } else {
                    sender.sendMessage("§cKit não encontrado.");
                }
            }
        }
    }
}
